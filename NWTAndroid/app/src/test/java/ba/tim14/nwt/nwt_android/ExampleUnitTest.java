package ba.tim14.nwt.nwt_android;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
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
//    @Test
//    public void getUsersWithGroupTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();

//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsersFromGroup(2L);
//
//        korisniciDobijeni.enqueue(new Callback<List<Korisnik>>() {
//            @Override
//            public void onResponse(Call<List<Korisnik>> call, Response<List<Korisnik>> response) {
//                List<Korisnik> korisnici = response.body();
//                System.out.println("TEST" + "Korisnici "+ korisnici);
//                assertNotNull(korisnici);
//            }
//            @Override
//            public void onFailure(Call<List<Korisnik>> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void getUserWithIdTest() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithId(2L);
//        dobijeniKorisnik.enqueue(new Callback<Korisnik>() {
//            @Override
//            public void onResponse(Call<Korisnik> call, Response<Korisnik> response) {
//                Korisnik korisnik = response.body();
//                System.out.println("TEST" + "Korisnici "+ korisnik);
//                assertNotNull(korisnik);
//            }
//            @Override
//            public void onFailure(Call<Korisnik> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
////        try {
////            Thread.sleep(1000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//    }
///*
//    @Test
//    public void doesUserWithIdExist() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithIdExist(2L);
//        dobijeniKorisnik.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String userExists = response.body().string();
//                    System.out.println("TEST" + "Korisnici "+ userExists);
//                    assertNotNull(userExists);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void doesUserWithUserNameExist() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist("jBauer");
//        dobijeniKorisnik.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String userExists = response.body().string();
//                    System.out.println("TEST" + "Korisnici "+ userExists);
//                    assertNotNull(userExists);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }   */
//
//    @Test
//    public void getUserWithUserNameTest() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithUserName("coBrian");
//
//        dobijeniKorisnik.enqueue(new Callback<Korisnik>() {
//            @Override
//            public void onResponse(Call<Korisnik> call, Response<Korisnik> response) {
//                Korisnik korisnik = response.body();
//                System.out.println("TEST" + "Korisnici "+ korisnik);
//                assertNotNull(korisnik);
//            }
//            @Override
//            public void onFailure(Call<Korisnik> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void startTripTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> tripStarted = locatorService.start("test123", Calendar.getInstance().getTimeInMillis(),1L);
//
//        tripStarted.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
//                String responseString;
//                try {
//                    if(response.isSuccessful()) {
//                        responseString = response.body().string();
//                        System.out.println("TEST: " + "trip " + responseString);
//                        assertNotNull(responseString);
//                    }
//                    else {
//                        String errorResponse = response.errorBody().string();
//                        System.out.println("TEST" + "trip " + errorResponse);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void stopTripTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> tripStopped = locatorService.stop(7L, Calendar.getInstance().getTimeInMillis(),26);
//
//        tripStopped.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
//                String responseString;
//                try {
//                    if(response.isSuccessful()) {
//                        responseString = response.body().string();
//                        System.out.println("TEST: " + "trip " + responseString);
//                        assertNotNull(responseString);
//                    }
//                    else {
//                        String errorResponse = response.errorBody().string();
//                        System.out.println("TEST: " + "trip " + errorResponse);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void getTripByNameTest(){
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//        final Putovanje[] currentTrip = new Putovanje[1];
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<Putovanje> putovanjeCall = locatorService.getTripByName("P1");
//
//        putovanjeCall.enqueue(new Callback<Putovanje>() {
//            @Override
//            public void onResponse(Call<Putovanje> call, Response<Putovanje> response) {
//                currentTrip[0] = response.body();
//                System.out.println( "TEST" + "getTripByName - Trip "+ currentTrip[0].getDistance());
//            }
//            @Override
//            public void onFailure(Call<Putovanje> call, Throwable t) {
//                System.out.println( "TEST" + "getTripByName - Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void addLocationTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> addedLocation = locatorService.addLocation(7L, Calendar.getInstance().getTimeInMillis(),47.232345,19.23332);
//
//        addedLocation.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
//                String responseString;
//                try {
//                    if(response.isSuccessful()) {
//                        responseString = response.body().string();
//                        System.out.println("TEST: " + "trip " + responseString);
//                        assertNotNull(responseString);
//                    }
//                    else {
//                        String errorResponse = response.errorBody().string();
//                        System.out.println("TEST:" + "trip " + errorResponse);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void getTripsByUserTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<List<Putovanje>> returnedTrips = locatorService.geAllTripsByUser(1L);
//
//        returnedTrips.enqueue(new Callback<List<Putovanje>>() {
//            @Override
//            public void onResponse(Call<List<Putovanje>> call, Response<List<Putovanje>> response) {
//                List<Putovanje> trips = response.body();
//                System.out.println("TEST: " + "trips "+ trips);
//                assertNotNull(trips);
//            }
//            @Override
//            public void onFailure(Call<List<Putovanje>> call, Throwable t) {
//                System.out.println("TEST" + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void getLocationsByTripTest() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<List<Lokacija>> returnedLocations = locatorService.getAllLocationsInATrip(7L);
//
//        returnedLocations.enqueue(new Callback<List<Lokacija>>() {
//            @Override
//            public void onResponse(Call<List<Lokacija>> call, Response<List<Lokacija>> response) {
//                List<Lokacija> locations = response.body();
//                System.out.println("TEST: " + "Lokacije "+ locations);
//                assertNotNull(locations);
//            }
//            @Override
//            public void onFailure(Call<List<Lokacija>> call, Throwable t) {
//                System.out.println("TEST: " + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void getLastKnownUserLocation() {
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLPutovanja)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<Lokacija> returnedLocation = locatorService.getLastLocationByUser(2L);
//
//        returnedLocation.enqueue(new Callback<Lokacija>() {
//            @Override
//            public void onResponse(Call<Lokacija> call, Response<Lokacija> response) {
//                Lokacija lastLocation = response.body();
//                System.out.println("TEST: " + "Lokacija "+ lastLocation);
//            }
//            @Override
//            public void onFailure(Call<Lokacija> call, Throwable t) {
//                System.out.println("TEST: " + "Nesto nije okej:  " + t.toString());
//            }
//        });
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void doesUserWithIdExist() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithIdExist(2L);
//
//        try {
//            Response<ResponseBody> response = dobijeniKorisnik.execute();
//            System.out.println("TEST" + "Korisnici "+ response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void doesUserWithUserNameExist() {
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl(Utils.URLKorisnici)
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        LocatorService locatorService = retrofit.create(LocatorService.class);
//        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist("jBauer");
//
//        try {
//            Response<ResponseBody> response = dobijeniKorisnik.execute();
//            System.out.println("TEST" + "Korisnici "+ response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }





    /////////////////////////////////////////////////////////AUTHORIZATION//////////////////////////////////////////////////

    private String authToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVSUQiOjIsIlVHcm91cCI6IkdydXBhMSIsInVzZXJfbmFtZSI6ImNvQnJpYW4iLCJzY29wZSI6WyJtb2JpbGUiXSwiVVR5cGUiOiJVU0VSIiwiZXhwIjoxNTI4MjgyMzQwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiNmM3OGIyZTktYTlmNy00MTIwLTk0ZDAtODQ3MWZiMDUyYTFlIiwiY2xpZW50X2lkIjoiY2xpZW50In0.EXw3YC9eg1ooXiMJ7DhwdRUh9Y8A46H2khQ-brJj468";
    private String tokenType="Bearer ";

    @Test
    public void getAuthTokenTest() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLOAuth)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> retrievedToken = locatorService.getToken("Basic Y2xpZW50OnNlY3JldA==","client","secret","password","mobile","coBrian","1234");

        retrievedToken.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                String responseString;
                try {
                    if(response.isSuccessful()) {
                        responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        System.out.println("TEST: " + "success: " + responseString);

                        System.out.println("TEST: " + "access_token: " + jsonObject.getString("access_token"));
                        System.out.println("TEST: " + "token_type: " + jsonObject.getString("token_type"));
                    }
                    else {
                        String errorResponse = response.errorBody().string();
                        System.out.println("TEST: " + "error: " + errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
    public void getUsersWithGroupTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Korisnik>> korisniciDobijeni = locatorService.getAllUsersFromGroup(tokenType + authToken,1L);

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
    public void getUserWithIdTestAuth() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithId(tokenType + authToken,2L);
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
    public void getUserWithUserNameTestAuth() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Korisnik> dobijeniKorisnik = locatorService.getUserWithUserName(tokenType + authToken,"coBrian");

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
    public void startTripTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStarted = locatorService.start(tokenType + authToken,"test123", Calendar.getInstance().getTimeInMillis(),1L);

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
    public void stopTripTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> tripStopped = locatorService.stop(tokenType + authToken,6L, Calendar.getInstance().getTimeInMillis(),26);

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
    public void getTripByNameTestAuth(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        final Putovanje[] currentTrip = new Putovanje[1];
        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Putovanje> putovanjeCall = locatorService.getTripByName(tokenType + authToken,"test123");

        putovanjeCall.enqueue(new Callback<Putovanje>() {
            @Override
            public void onResponse(Call<Putovanje> call, Response<Putovanje> response) {
                currentTrip[0] = response.body();
                System.out.println( "TEST" + "getTripByName - Trip "+ currentTrip[0]);
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
    public void addLocationTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> addedLocation = locatorService.addLocation(tokenType + authToken,6L, Calendar.getInstance().getTimeInMillis(),47.232345,19.23332);

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
    public void getTripsByUserTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Putovanje>> returnedTrips = locatorService.geAllTripsByUser(tokenType + authToken,1L);

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
    public void getLocationsByTripTestAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<List<Lokacija>> returnedLocations = locatorService.getAllLocationsInATrip(tokenType + authToken,6L);

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
    public void getLastKnownUserLocationAuth() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLPutovanja)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<Lokacija> returnedLocation = locatorService.getLastLocationByUser(tokenType + authToken,1L);

        returnedLocation.enqueue(new Callback<Lokacija>() {
            @Override
            public void onResponse(Call<Lokacija> call, Response<Lokacija> response) {
                Lokacija lastLocation = response.body();
                System.out.println("TEST: " + "Lokacija "+ lastLocation);
            }
            @Override
            public void onFailure(Call<Lokacija> call, Throwable t) {
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
    public void doesUserWithIdExistAuth() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithIdExist(tokenType + authToken,2L);

        try {
            Response<ResponseBody> response = dobijeniKorisnik.execute();
            System.out.println("TEST" + "Korisnici "+ response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doesUserWithUserNameExistAuth() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Utils.URLKorisnici)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        LocatorService locatorService = retrofit.create(LocatorService.class);
        Call<ResponseBody> dobijeniKorisnik = locatorService.doesUserWithUsernameExist(tokenType + authToken,"jBauer");

        try {
            Response<ResponseBody> response = dobijeniKorisnik.execute();
            System.out.println("TEST" + "Korisnici "+ response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}