package app.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import app.config.HibernateConfig;
import app.dtos.CastMemberDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDTO;
import app.entities.Genre;
import app.entities.Movie;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MovieDAOTest {

    private static EntityManagerFactory emfTest;
    private static MovieDAO movieDAO;

    private List<GenreDTO> genreDTOs;
    private List<MovieDTO> movieDTOs;
    private List<CastMemberDTO> castMemberDTOs;

    @BeforeAll
    static void beforeAll() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emfTest);
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

        castMemberDTOs = List.of(
                new CastMemberDTO(
                        109,
                        "Elijah Wood",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        1327,
                        "Ian McKellen",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        108,
                        "Peter Jackson",
                        "Directing",
                        "Director"
                ),
                new CastMemberDTO(
                        1328,
                        "Sean Astin",
                        "Acting",
                        null
                ),
                new CastMemberDTO(
                        655,
                        "John Rhys-Davies",
                        "Acting",
                        null
                )
        );

        movieDTOs = List.of(
                new MovieDTO(
                        120,
                        "The Lord of the Rings: The Fellowship of the Ring",
                        "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.",
                        174.601,
                        LocalDate.of(2001, 12, 18),
                        8.4,
                        Set.of(genreDTOs.get(0).getId(), genreDTOs.get(1).getId(), genreDTOs.get(2).getId()),
                        Set.of(castMemberDTOs.get(0), castMemberDTOs.get(1))
                ),
                new MovieDTO(
                        121,
                        "The Lord of the Rings: The Two Towers",
                        "Frodo Baggins and the other members of the Fellowship continue on their sacred quest to destroy the One Ring--but on separate paths. Their destinies lie at two towers--Orthanc Tower in Isengard, where the corrupt wizard Saruman awaits, and Sauron's fortress at Barad-dur, deep within the dark lands of Mordor. Frodo and Sam are trekking to Mordor to destroy the One Ring of Power while Gimli, Legolas and Aragorn search for the orc-captured Merry and Pippin. All along, nefarious wizard Saruman awaits the Fellowship members at the Orthanc Tower in Isengard.",
                        124.454,
                        LocalDate.of(2002, 12, 18),
                        8.398,
                        Set.of(genreDTOs.get(0).getId(), genreDTOs.get(1).getId(), genreDTOs.get(2).getId()),
                        Set.of(castMemberDTOs.get(2), castMemberDTOs.get(3), castMemberDTOs.get(4))
                )
        );

        movieDTOs.forEach(movie -> movie.getCast().forEach(castMemberDTO -> castMemberDTO.addMovieId(movie.getId())));

        try (EntityManager em = emfTest.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("DELETE FROM CastMember").executeUpdate();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createQuery("DELETE FROM Genre").executeUpdate();

            genreDTOs.stream().map(GenreDTO::getAsEntity).forEach(em::persist);
            castMemberDTOs.stream().map(CastMemberDTO::getAsEntity).forEach(em::persist);
            movieDTOs.stream().map(movieDTO -> {
                Set<Genre> foundGenres = new HashSet<>();
                Movie movie = movieDTO.getAsEntity();

                movieDTO.getGenreIds().forEach(genreId -> {
                    Genre foundGenre = em.find(Genre.class, genreId);
                    foundGenres.add(foundGenre);
                });

                movie.setGenres(foundGenres);

                return movie;
            }).forEach(em::persist);

            em.getTransaction().commit();
        }
    }

    @Test
    void testGetById() {
        MovieDTO expected = movieDTOs.get(0);

        MovieDTO actual = movieDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetAll() {
        Set<MovieDTO> expected = new HashSet<>(movieDTOs);
        Set<MovieDTO> actual = movieDAO.getAll();

        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testCreate() {
        MovieDTO movie = new MovieDTO(
                122,
                "The Lord of the Rings: The Return of the King",
                "As armies mass for a final battle that will decide the fate of the world--and powerful, ancient forces of Light and Dark compete to determine the outcome--one member of the Fellowship of the Ring is revealed as the noble heir to the throne of the Kings of Men. Yet, the sole hope for triumph over evil lies with a brave hobbit, Frodo, who, accompanied by his loyal friend Sam and the hideous, wretched Gollum, ventures deep into the very dark heart of Mordor on his seemingly impossible quest to destroy the Ring of Power.",
                223.304,
                LocalDate.of(2003, 12, 17),
                8.48,
                Set.of(genreDTOs.get(0).getId(), genreDTOs.get(1).getId(), genreDTOs.get(2).getId()),
                Set.of(
                        new CastMemberDTO(
                                65,
                                "Ian Holm",
                                "Acting",
                                null
                        ),
                        new CastMemberDTO(
                                48,
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
    void testUpdate() {
        MovieDTO expected = movieDTOs.get(0);

        expected.setOriginalTitle("The Lord of the Rings: The Movie");
        expected.setReleaseDate(LocalDate.of(2004, 7, 22));

        MovieDTO actual = movieDAO.update(expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testDelete() {
        MovieDTO movie = movieDTOs.get(0);

        movieDAO.delete(movie.getId());

        Assertions.assertThrowsExactly(EntityNotFoundException.class, () -> movieDAO.getById(movie.getId()));
    }
}