package org.example.daos;

import jakarta.persistence.*;
import org.example.dtos.GenreDTO;
import org.example.entities.Genre;

import java.util.Set;
import java.util.stream.Collectors;

public class GenreDAO implements IDAO<GenreDTO> {
    private EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public GenreDTO getById(Long id) {
        Genre found;
        try (EntityManager em = emf.createEntityManager()) {
            found = em.find(Genre.class, id);
            if (found == null) {
                throw new EntityNotFoundException("Genre with id " + id + " not found");
            }
        }
        return new GenreDTO(found);
    }

    @Override
    public Set<GenreDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
            Set<Genre> result = query.getResultList().stream().collect(Collectors.toSet());
            return result.stream().map(genre -> new GenreDTO(genre)).collect(Collectors.toSet());

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("List of genre not found");
        }
    }

    @Override
    public GenreDTO create(GenreDTO genreDTO) {
        Genre genre = genreDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(genre);

            em.getTransaction().commit();
        }
        return new GenreDTO(genre);
    }

    @Override
    public GenreDTO update(GenreDTO genreDTO) {
        Genre genre = genreDTO.getAsEntity();
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre foundGenre = em.find(Genre.class, genre.getId());

            if (genre.getName() != null) {
                foundGenre.setName(genre.getName());
            }
            em.getTransaction().commit();
            return new GenreDTO(foundGenre);

        } catch (NoResultException e) {
            throw new EntityNotFoundException(String.format("Could not find genre with id '%d' in database.", genre.getId()), e);
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Genre genre = em.find(Genre.class, id);
            if (genre == null) {
                throw new EntityNotFoundException("Genre with id " + id + " not found");
            }

            em.getTransaction().begin();
            em.remove(genre);
            em.getTransaction().commit();

        } catch (RollbackException e) {
            throw new RollbackException(String.format("Could not delete genre with id %d.", id), e);
        }
    }
}
