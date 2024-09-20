package app.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import app.config.HibernateConfig;
import app.dtos.GenreDTO;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GenreDAOTest {
    private static EntityManagerFactory emfTest;
    private static GenreDAO genreDAO;

    private List<GenreDTO> genreDTOs;

    @BeforeAll
    static void beforeAll() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        genreDAO = new GenreDAO(emfTest);
    }

    @AfterAll
    public static void tearDown() {
        emfTest.close();
    }

    @BeforeEach
    void setUp() {
        genreDTOs = List.of(
                new GenreDTO(12, "Adventure"),
                new GenreDTO(14, "Fantasy"),
                new GenreDTO(28, "Action")
        );

        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM Genre").executeUpdate();

            genreDTOs.stream().map(GenreDTO::getAsEntity).forEach(em::persist);

            em.getTransaction().commit();
        }
    }

    @Test
    void testGetById() {
        GenreDTO expected = genreDTOs.get(0);
        GenreDTO actual = genreDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetAll() {
        Set<GenreDTO> expected = new HashSet<>(genreDTOs);
        Set<GenreDTO> actual = genreDAO.getAll();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCreate() {
        GenreDTO genre = new GenreDTO(18, "Drama");

        GenreDTO expected = genreDAO.create(genre);
        GenreDTO actual = genreDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testUpdate() {
        GenreDTO expected = genreDTOs.get(0);

        expected.setName("Drama");

        GenreDTO actual = genreDAO.update(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testDelete() {
        GenreDTO genre = genreDTOs.get(0);

        genreDAO.delete(genre.getId());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> genreDAO.getById(genre.getId()));
    }
}