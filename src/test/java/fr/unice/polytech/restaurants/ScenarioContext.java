package fr.unice.polytech.restaurants;

import fr.unice.polytech.restaurants.Restaurant;

/**
 * Context partagé entre tous les steps d'un scénario Cucumber.
 * PicoContainer injecte automatiquement la même instance dans tous les steps.
 */
public class ScenarioContext {
    public Restaurant restaurant;
    public boolean managerLoggedIn;
}