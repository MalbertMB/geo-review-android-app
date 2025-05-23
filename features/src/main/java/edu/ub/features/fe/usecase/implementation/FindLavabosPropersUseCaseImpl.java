package edu.ub.features.fe.usecase.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.features.fe.repositories.ToiletRepository;
import edu.ub.features.fe.usecase.FindLavabosPropersUseCase;
import io.reactivex.rxjava3.core.Observable;

public class FindLavabosPropersUseCaseImpl implements FindLavabosPropersUseCase {

    private final ToiletRepository db;
    private static final int EARTH_RADIUS_KM = 6371; // Updated to standard Earth radius

    public FindLavabosPropersUseCaseImpl(ToiletRepository toiletRepository) {
        this.db = toiletRepository;
    }
    //Aqui es crea aquesta estructura de dades
    // com a classe per reduir el cost computacional
    static class ToiletWithDistance {
        final Toilet toilet;
        final double distance;

        ToiletWithDistance(Toilet toilet, double distance) {
            this.toilet = toilet;
            this.distance = distance;
        }
    }

    public Observable<List<Toilet>> execute(double userLat, double userLong, double radiusKm) {
        return db.getAllToilet()
                .map(toilets -> processToilets(toilets, userLat, userLong, radiusKm));
    }

    //LLOGICA DE TRIATGE
    private List<Toilet> processToilets(List<Toilet> toilets, double userLat, double userLong, double radius) {
        List<ToiletWithDistance> filtered = new ArrayList<>();

        // FILTREM LAVABOS I CALCULEM DISTÂNCIA AL USUARI
        for (Toilet t : toilets) {
            double distance = calculateDistance(t.getLatitude(), t.getLongitude(), userLat, userLong);
            if (distance <= radius) {
                filtered.add(new ToiletWithDistance(t, distance));
            }
        }

        // TORNEM A TRIAR SEGONS "PUNTUACIÓ"
        Collections.sort(filtered, (t1, t2) ->
                Float.compare(calculateScore(t2), calculateScore(t1))
        );

        //DELS TRIATS AGAFEM NOMES LA VARIABLE QUE INTERESSA, TOILET
        List<Toilet> result = new ArrayList<>();
        for (ToiletWithDistance twd : filtered) {
            result.add(twd.toilet);
        }

        return result;
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    /// Mètodes triatge
    ///////////////////////////////////////////////////////////////////////////////////////////

    private float calculateScore(ToiletWithDistance twd) {
        float rating = twd.toilet.getRatingAverage();
        float distance = (float)twd.distance;

        //Normalitzem números
        //fem que siguin números del 0-> 5
        float distanceNorm = (float) (5 * ((distance / 1000.0)));

        //llogica de la fòrmula
        //a partir dels >300
        float puntuacio = 0;

        if(distance <= 300 ){
            puntuacio = (float)(Math.pow(rating, 2.5) / (distanceNorm*0.9));
            return puntuacio;

        }else{
            puntuacio = (float)(Math.pow(rating, 1.5) / (distanceNorm*1.8));
            return puntuacio;

        }

    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) *
                        Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return EARTH_RADIUS_KM * c;
    }
}