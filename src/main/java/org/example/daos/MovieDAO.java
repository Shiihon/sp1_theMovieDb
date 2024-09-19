package org.example.daos;

import jakarta.persistence.*;
import org.example.dtos.MovieDTO;
import org.example.entities.CastMember;
import org.example.entities.Genre;
import org.example.entities.Movie;
import org.hibernate.Hibernate;

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

    public Double getAverageRating() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Double> query = em.createQuery("SELECT AVG(m.voteAverage) FROM Movie m", Double.class);
            return query.getSingleResult();
        }
    }

    public Set<MovieDTO> getTopTenHighest (){
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.voteAverage DESC", Movie.class);
           return query.setMaxResults(10).getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
        }
    }

    public Set<MovieDTO> getTopTenLowest (){
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.voteAverage", Movie.class);
            return query.setMaxResults(10).getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
        }
    }

    public Set<MovieDTO> getTopTenMostPopular (){
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class);
            return query.setMaxResults(10).getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
        }
    }

    public Set<MovieDTO> getMovieByTitle(String originalTitle) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.originalTitle) LIKE LOWER(:originalTitle)", Movie.class);
            query.setParameter("originalTitle", "%" + originalTitle + "%");

            return query.getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
        }
    }

    public Set<MovieDTO> getAllWithDetails() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createNamedQuery("Movie.getAll", Movie.class);
            Set<Movie> movieResult = query.getResultStream().collect(Collectors.toSet());

            movieResult.forEach(movie -> {
                movie.getCast().forEach(castMember -> Hibernate.initialize(castMember.getMovies()));
                Hibernate.initialize(movie.getCast());
                Hibernate.initialize(movie.getGenres());
            });

            return movieResult.stream().map(MovieDTO::new).collect(Collectors.toSet());
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
                    Hibernate.initialize(foundCastMember.getMovies());
                    foundCastMembers.add(foundCastMember);
                } else {
                    foundCastMembers.add(castMember);
                    em.persist(castMember);
                }
            });

            foundCastMembers.forEach(castMember -> castMember.addMovie(movie));

            movie.setCast(foundCastMembers);
            movie.setGenres(getMovieGenres(movieDTO));

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
                foundMovie.setGenres(getMovieGenres(movieDTO));
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

    private List<Genre> getMovieGenres(MovieDTO movieDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Genre> foundGenres = new ArrayList<>();

            movieDTO.getGenreIds().forEach(genreId -> {
                TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g WHERE g.id = :id", Genre.class);
                query.setParameter("id", genreId);
                query.setMaxResults(1);

                try {
                    foundGenres.add(query.getSingleResult());
                } catch (NoResultException e) {
                    throw new EntityNotFoundException(String.format("Could not find genre '%s' in database.", genreId), e);
                }
            });

            return foundGenres;
        }
    }

    public Set<MovieDTO> getMoviesWithinGenre(Long genreId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Movie> query = em.createQuery("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId", Movie.class);
            query.setParameter("genreId", genreId);

            return query.getResultStream().map(MovieDTO::new).collect(Collectors.toSet());
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
