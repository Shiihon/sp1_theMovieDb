package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.config.HibernateConfig;
import org.example.dtos.GenreDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GenreDAOTest {
    private static EntityManagerFactory emf;
    private static GenreDAO genreDAO;

    private List<GenreDTO> genreDTOS;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        genreDAO = new GenreDAO(emf);
    }

    @BeforeEach
    void setUp() {
        genreDTOS = List.of(
                new GenreDTO(12L, "Adventure"),
                new GenreDTO(14L, "Fantasy"),
                new GenreDTO(28L, "Action")
        );
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Genre").executeUpdate();

            genreDTOS.stream().map(GenreDTO::getAsEntity).forEach(em::persist);

            em.getTransaction().commit();
        }
    }

    @Test
    void getById() {
        GenreDTO expected = genreDTOS.get(0);
        GenreDTO actual = genreDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAll() {
        Set<GenreDTO> expected = new HashSet<>(genreDTOS);
        Set<GenreDTO> actual = genreDAO.getAll();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void create() {
        GenreDTO genre = new GenreDTO(18L, "Drama");

        GenreDTO expected = genreDAO.create(genre);
        GenreDTO actual = genreDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        GenreDTO expected = genreDTOS.get(0);

        expected.setName("Drama");

        GenreDTO actual = genreDAO.update(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        GenreDTO genre = genreDTOS.get(0);

        genreDAO.delete(genre.getId());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> genreDAO.getById(genre.getId()));
    }
}