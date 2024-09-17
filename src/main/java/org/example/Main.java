package org.example;

import jakarta.persistence.EntityManagerFactory;
import org.example.config.HibernateConfig;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("persons");
    }
}