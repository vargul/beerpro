package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;
import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.FridgeItem;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.domain.models.MyBeerFromFridge;
import ch.beerpro.domain.models.MyBeerFromRating;
import ch.beerpro.domain.models.MyBeerFromWishlist;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;

import static androidx.lifecycle.Transformations.map;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class MyBeersRepository {

    public static List<MyBeer> getAllMyBeers(Quartet<List<FridgeItem>, List<Wish>, List<Rating>, HashMap<String, Beer>> input ) {
        List<FridgeItem> fridge = input.getValue0();
        List<Wish> wishlist = input.getValue1();
        List<Rating> ratings = input.getValue2();
        HashMap<String, Beer> beers = input.getValue3();

        ArrayList<MyBeer> result = new ArrayList<>();
        Set<String> beersAlreadyOnTheList = new HashSet<>();

        if (fridge != null) {
            for (FridgeItem fridgeItem : fridge) {
                String beerId = fridgeItem.getBeerId();
                result.add(new MyBeerFromFridge(fridgeItem, beers.get(beerId)));
                beersAlreadyOnTheList.add(beerId);
            }
        }

        if (wishlist != null) {
            for (Wish wish : wishlist) {
                String beerId = wish.getBeerId();
                if(!beersAlreadyOnTheList.contains(beerId)){
                    result.add(new MyBeerFromWishlist(wish, beers.get(beerId)));
                    beersAlreadyOnTheList.add(beerId);
                }
            }
        }

        if (ratings != null) {
            for (Rating rating : ratings) {
                String beerId = rating.getBeerId();
                if (!beersAlreadyOnTheList.contains(beerId)) {
                    result.add(new MyBeerFromRating(rating, beers.get(beerId)));
                    beersAlreadyOnTheList.add(beerId);
                }
            }
        }

        Collections.sort(result, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return result;
    }

    public LiveData<List<MyBeer>> getAllMyBeers(LiveData<List<Beer>> allBeers,
                                                LiveData<List<FridgeItem>> myFridge,
                                                LiveData<List<Wish>> myWishlist,
                                                LiveData<List<Rating>> myRatings) {
        return map(combineLatest(myFridge, myWishlist, myRatings,
                map(allBeers, Entity::entitiesById)), MyBeersRepository::getAllMyBeers);
    }

}
