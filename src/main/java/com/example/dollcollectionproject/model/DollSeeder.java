package com.example.dollcollectionproject.model;

import java.util.ArrayList;
import java.util.List;

public class DollSeeder {

    public static List<Doll> getSeedData() {
        List<Doll> initialDolls = new ArrayList<>();

        // Adding sample dolls to the list
        // Format: Name, Year, Brand, ImagePath, Description
        initialDolls.add(new Doll(
                "Tradwaifu Juggler",
                1960,
                "Barbie",
                "closet/1960_fashion_queen.jpg"
        ));

        initialDolls.add(new Doll(
                "Hawaii Doing",
                1984,
                "Barbie",
                "closet/1984_beach_time.jpg"
        ));

        initialDolls.add(new Doll(
                "Eye Sickle",
                2001,
                "Barbie",
                "closet/2001_collection_aqua_blue.jpg"
        ));

        initialDolls.add(new Doll(
                "Candace Came",
                2019,
                "Barbie",
                "closet/2019_holiday_red_white.jpg"
        ));

        return initialDolls;
    }
}