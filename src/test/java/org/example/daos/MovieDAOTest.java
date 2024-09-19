package org.example.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.config.HibernateConfig;
import org.example.dtos.CastMemberDTO;
import org.example.dtos.GenreDTO;
import org.example.dtos.MovieDTO;
import org.example.entities.Genre;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MovieDAOTest {

    private static EntityManagerFactory emf;
    private static MovieDAO movieDAO;

    private List<GenreDTO> genreDTOS;
    private List<MovieDTO> movieDTOS;
    private List<CastMemberDTO> castMemberDTOs;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);
    }

    @BeforeEach
    void setUp() {
        genreDTOS = List.of(
                new GenreDTO(12L, "Adventure"),
                new GenreDTO(14L, "Fantasy"),
                new GenreDTO(28L, "Action")
        );

        castMemberDTOs = List.of(
                new CastMemberDTO(
                        109L,
                        "Elijah Wood",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        1327L,
                        "Ian McKellen",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        108L,
                        "Peter Jackson",
                        "Directing",
                        "Director"
                ),
                new CastMemberDTO(
                        1328L,
                        "Sean Astin",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        655L,
                        "John Rhys-Davies",
                        "Acting",
                        null
                )
        );

        movieDTOS = List.of(
                new MovieDTO(
                        120L,
                        "The Lord of the Rings: The Fellowship of the Ring",
                        "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.",
                        174.601,
                        LocalDate.of(2001, 12, 18),
                        8.4,
                        List.of(genreDTOS.get(0), genreDTOS.get(1), genreDTOS.get(2)),
                        List.of(castMemberDTOs.get(0), castMemberDTOs.get(1), castMemberDTOs.get(2))
                ),
                new MovieDTO(
                        121L,
                        "The Lord of the Rings: The Two Towers",
                        "Frodo Baggins and the other members of the Fellowship continue on their sacred quest to destroy the One Ring--but on separate paths. Their destinies lie at two towers--Orthanc Tower in Isengard, where the corrupt wizard Saruman awaits, and Sauron's fortress at Barad-dur, deep within the dark lands of Mordor. Frodo and Sam are trekking to Mordor to destroy the One Ring of Power while Gimli, Legolas and Aragorn search for the orc-captured Merry and Pippin. All along, nefarious wizard Saruman awaits the Fellowship members at the Orthanc Tower in Isengard.",
                        124.454,
                        LocalDate.of(2002, 12, 18),
                        8.398,
                        List.of(genreDTOS.get(0), genreDTOS.get(1), genreDTOS.get(2)),
                        List.of(castMemberDTOs.get(2), castMemberDTOs.get(3), castMemberDTOs.get(4))
                )
        );

        movieDTOS.forEach(movie -> movie.getCast().forEach(castMemberDTO -> castMemberDTO.addMovieDTO(movie.getId())));

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM CastMember").executeUpdate();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createQuery("DELETE FROM Genre").executeUpdate();

            genreDTOS.stream().map(Genre::new).forEach(em::persist);
            movieDTOS.forEach(movie -> movie.getCast().stream().map(CastMemberDTO::getAsEntity).forEach(em::persist));
            movieDTOS.stream().map(MovieDTO::getAsEntity).forEach(em::persist);

            em.getTransaction().commit();
        }
    }

    @Test
    void getById() {
        MovieDTO expected = movieDTOS.get(0);
        MovieDTO actual = movieDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAll() {
        Set<MovieDTO> expected = new HashSet<>(movieDTOS);
        Set<MovieDTO> actual = movieDAO.getAll();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void create() {
        MovieDTO movie = new MovieDTO(
                122L,
                "The Lord of the Rings: The Return of the King",
                "As armies mass for a final battle that will decide the fate of the world--and powerful, ancient forces of Light and Dark compete to determine the outcome--one member of the Fellowship of the Ring is revealed as the noble heir to the throne of the Kings of Men. Yet, the sole hope for triumph over evil lies with a brave hobbit, Frodo, who, accompanied by his loyal friend Sam and the hideous, wretched Gollum, ventures deep into the very dark heart of Mordor on his seemingly impossible quest to destroy the Ring of Power.",
                223.304,
                LocalDate.of(2003, 12, 17),
                8.48,
                List.of(genreDTOS.get(0), genreDTOS.get(1), genreDTOS.get(2)),
                List.of(
                        new CastMemberDTO(
                                65L,
                                "Ian Holm",
                                "Acting",
                                null
                        ),
                        new CastMemberDTO(
                                48L,
                                "Sean Bean",
                                "Acting",
                                null
                        )
                )
        );

        MovieDTO expected = movieDAO.create(movie);
        MovieDTO actual = movieDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        MovieDTO expected = movieDTOS.get(0);

        expected.setOriginalTitle("The Lord of the Rings: The Movie");
        expected.setReleaseDate(LocalDate.of(2004, 7, 22));

        MovieDTO actual = movieDAO.update(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        MovieDTO movie = movieDTOS.get(0);

        movieDAO.delete(movie.getId());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> movieDAO.getById(movie.getId()));
    }
}