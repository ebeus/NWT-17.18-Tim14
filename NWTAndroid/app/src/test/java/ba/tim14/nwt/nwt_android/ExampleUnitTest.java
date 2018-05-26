package ba.tim14.nwt.nwt_android;

import android.util.Log;

import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import ba.tim14.nwt.nwt_android.api.LocatorService;
import ba.tim14.nwt.nwt_android.classes.Korisnik;
import ba.tim14.nwt.nwt_android.classes.Lokacija;
import ba.tim14.nwt.nwt_android.classes.Putovanje;
import ba.tim14.nwt.nwt_android.classes.Trip;
import ba.tim14.nwt.nwt_android.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void getUsersWithGroupTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsersFromGroup(2L);

        korisniciDobijeni.enqueue(new Callback<List<Korisnik>>() {
            @Override
            public void onResponse(Call<List<Korisnik>> call, Response<List<Korisnik>> response) {
                List<Korisnik> korisnici = response.body();
                System.out.println("TEST" + "Korisnici "+ korisnici);
                assertNotNull(korisnici);
            }
            @Override
            public void onFailure(Call<List<Korisnik>> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getUserWithIdTest() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithId(2L);
        dobijeniKorisnik.enqueue(new Callback<Korisnik>() {
            @Override
            public void onResponse(Call<Korisnik> call, Response<Korisnik> response) {
                Korisnik korisnik = response.body();
                System.out.println("TEST" + "Korisnici "+ korisnik);
                assertNotNull(korisnik);
            }
            @Override
            public void onFailure(Call<Korisnik> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/*
    @Test
    public void doesUserWithIdExist() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithIdExist(2L);
        dobijeniKorisnik.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String userExists = response.body().string();
                    System.out.println("TEST" + "Korisnici "+ userExists);
                    assertNotNull(userExists);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doesUserWithUserNameExist() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist("jBauer");
        dobijeniKorisnik.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String userExists = response.body().string();
                    System.out.println("TEST" + "Korisnici "+ userExists);
                    assertNotNull(userExists);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }   */

    @Test
    public void getUserWithUserNameTest() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithUserName("coBrian");

        dobijeniKorisnik.enqueue(new Callback<Korisnik>() {
            @Override
            public void onResponse(Call<Korisnik> call, Response<Korisnik> response) {
                Korisnik korisnik = response.body();
                System.out.println("TEST" + "Korisnici "+ korisnik);
                assertNotNull(korisnik);
            }
            @Override
            public void onFailure(Call<Korisnik> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void startTripTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStarted = locatorService.start("test123", Calendar.getInstance().getTimeInMillis(),1L);

        tripStarted.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        System.out.println("TEST: " + "trip " + responseString);
                        assertNotNull(responseString);
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        System.out.println("TEST" + "trip " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void stopTripTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStopped = locatorService.stop(7L, Calendar.getInstance().getTimeInMillis(),26);

        tripStopped.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        System.out.println("TEST: " + "trip " + responseString);
                        assertNotNull(responseString);
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        System.out.println("TEST: " + "trip " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTripByNameTest(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        final Putovanje[] currentTrip = new Putovanje[1];
        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Putovanje> putovanjeCall = locatorService.getTripByName("P1");

        putovanjeCall.enqueue(new Callback<Putovanje>() {
            @Override
            public void onResponse(Call<Putovanje> call, Response<Putovanje> response) {
                currentTrip[0] = response.body();
                System.out.println( "TEST" + "getTripByName - Trip "+ currentTrip[0].getDistance());
            }
            @Override
            public void onFailure(Call<Putovanje> call, Throwable t) {
                System.out.println( "TEST" + "getTripByName - Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addLocationTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> addedLocation = locatorService.addLocation(7L, Calendar.getInstance().getTimeInMillis(),47.232345,19.23332);

        addedLocation.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        System.out.println("TEST: " + "trip " + responseString);
                        assertNotNull(responseString);
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        System.out.println("TEST:" + "trip " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getTripsByUserTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Putovanje>> returnedTrips = locatorService.geAllTripsByUser(1L);

        returnedTrips.enqueue(new Callback<List<Putovanje>>() {
            @Override
            public void onResponse(Call<List<Putovanje>> call, Response<List<Putovanje>> response) {
                List<Putovanje> trips = response.body();
                System.out.println("TEST: " + "trips "+ trips);
                assertNotNull(trips);
            }
            @Override
            public void onFailure(Call<List<Putovanje>> call, Throwable t) {
                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLocationsByTripTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Lokacija>> returnedLocations = locatorService.getAllLocationsInATrip(7L);

        returnedLocations.enqueue(new Callback<List<Lokacija>>() {
            @Override
            public void onResponse(Call<List<Lokacija>> call, Response<List<Lokacija>> response) {
                List<Lokacija> locations = response.body();
                System.out.println("TEST: " + "Lokacije "+ locations);
                assertNotNull(locations);
            }
            @Override
            public void onFailure(Call<List<Lokacija>> call, Throwable t) {
                System.out.println("TEST: " + "Nesto nije okej:  " + t.toString());
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doesUserWithIdExist() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithIdExist(2L);

        try {
            Response<ResponseBody> response = dobijeniKorisnik.execute();
            System.out.println("TEST" + "Korisnici "+ response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doesUserWithUserNameExist() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist("jBauer");

        try {
            Response<ResponseBody> response = dobijeniKorisnik.execute();
            System.out.println("TEST" + "Korisnici "+ response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}