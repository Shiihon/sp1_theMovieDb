package org.example.daos;

import jakarta.persistence.*;
import org.example.dtos.MovieDTO;
import org.example.entities.CastMember;
import org.example.entities.Genre;
import org.example.entities.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieDAO implements IDAO<MovieDTO> {

    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public MovieDTO getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);

            if (movie != null) {
                return new MovieDTO(movie);
            } else {
                throw new EntityNotFoundException(String.format("Movie with id %d could not be found.", id));
            }
        }
    }

    @Override
    public Set<MovieDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createNamedQuery("Movie.getAll", Movie.class);
            return query.getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
        }
    }

    @Override
    public MovieDTO create(MovieDTO movieDTO) {
        Movie movie = movieDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            List<CastMember> foundCastMembers = new ArrayList<>();

            movie.getCast().forEach(castMember -> {
                CastMember foundCastMember = em.find(CastMember.class, castMember.getId());

                if (foundCastMember != null) {
                    foundCastMembers.add(foundCastMember);
                } else {
                    foundCastMembers.add(castMember);
                    em.persist(castMember);
                }
            });

            foundCastMembers.forEach(castMember -> castMember.addMovie(movie));

            movie.setCast(foundCastMembers);
            movie.setGenres(getMovieGenres(movie));

            em.persist(movie);
            em.getTransaction().commit();
        }

        return new MovieDTO(movie);
    }

    @Override
    public MovieDTO update(MovieDTO movieDTO) {
        Movie movie = movieDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Movie foundMovie = em.find(Movie.class, movie.getId());

            if (movie.getOriginalTitle() != null) {
                foundMovie.setOriginalTitle(movie.getOriginalTitle());
            }
            if (movie.getOverview() != null) {
                foundMovie.setOverview(movie.getOverview());
            }
            if (movie.getPopularity() != null) {
                foundMovie.setPopularity(movie.getPopularity());
            }
            if (movie.getReleaseDate() != null) {
                foundMovie.setReleaseDate(movie.getReleaseDate());
            }
            if (movie.getVoteAverage() != null) {
                foundMovie.setVoteAverage(movie.getVoteAverage());
            }
            if (!movie.getGenres().isEmpty()) {
                foundMovie.setGenres(getMovieGenres(movie));
            }
            if (!movie.getCast().isEmpty()) {
                List<CastMember> foundCastMembers = new ArrayList<>();

                movie.getCast().forEach(castMember -> {
                    CastMember foundCastMember = em.find(CastMember.class, castMember.getId());

                    if (foundCastMember != null) {
                        foundCastMembers.add(foundCastMember);
                    } else {
                        throw new EntityNotFoundException(String.format("A CastMember with id %d could not be found.", castMember.getId()));
                    }
                });

                foundMovie.setCast(foundCastMembers);
            }

            em.getTransaction().commit();
            return new MovieDTO(foundMovie);
        }
    }

    private List<Genre> getMovieGenres(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Genre> foundGenres = new ArrayList<>();

            movie.getGenres().forEach(genre -> {
                TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.id = :id AND lower(g.name) = lower(:name)", Genre.class);
                query.setParameter("id", genre.getId());
                query.setParameter("name", genre.getName());
                query.setMaxResults(1);

                try {
                    foundGenres.add(query.getSingleResult());
                } catch (NoResultException e) {
                    throw new EntityNotFoundException(String.format("Could not find genre '%s' in database.", genre), e);
                }
            });

            return foundGenres;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Movie movie = em.find(Movie.class, id);

            em.getTransaction().begin();
            em.remove(movie);
            em.getTransaction().commit();
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Could not delete movie with id %d.", id), e);
        }
    }
}
