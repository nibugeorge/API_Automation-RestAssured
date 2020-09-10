import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static io.restassured.RestAssured.given;


public class DogsAPITests {

    String baseURL="https://dog.ceo/api/";

    @Test()
    public void listAllTest() {
        try{
            Response response=given().log().all().when().get(baseURL+"breeds/list/all");
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(),200);
            Assert.assertEquals(response.jsonPath().getString("status"),"success");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider="Breeds")
    public void breedImageTest(String breed,int errorCode,String status) {
        try{
            int count=0;
            Response response=given().log().all().when().get(baseURL+"breed/"+breed+"/images");
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(),errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            if(errorCode==200){
                List<String> jsonResponse = response.jsonPath().getList("message");
                for(int i=0;i<jsonResponse.size();i++){
                    Assert.assertEquals(isValidURL(jsonResponse.get(i)),true);
                    count++;
                }
                System.out.println(count);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider="Breeds")
    public void breedImageRandomTest(String breed,int errorCode,String status) {
        try{
            Response response=given().log().all().when().get(baseURL+"breed/"+breed+"/images/random");
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(),errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            if(errorCode==200) {
                Assert.assertEquals(isValidURL(response.jsonPath().getString("message")), true);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider="Count")
    public void breedImageRandomCountTest(double count,int errorCode,String status) {
        try{
            Response response=given().log().all().when().get(baseURL+"breed/hound/images/random/"+count);
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(), errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            List<String> jsonResponse = response.jsonPath().getList("message");
            Assert.assertEquals(isValidURL(jsonResponse.get(0)),true);
            if(count>1000){
                Assert.assertEquals(jsonResponse.size(),1000);//max limit is 1000 for hounds images
            }
            else {
                Assert.assertEquals(jsonResponse.size(),(int)count);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider = "SubBreeds")
    public void subreedImageTest(String breed,String subreed,int errorCode,String status) {
        try{
            int count=0;
            Response response=given().log().all().when().get(baseURL+"breed/"+breed+"/"+subreed+"/images");
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(),errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            if(errorCode==200){
                List<String> jsonResponse = response.jsonPath().getList("message");
                for(int i=0;i<jsonResponse.size();i++){
                    Assert.assertEquals(isValidURL(jsonResponse.get(i)),true);
                    count++;
                }
                System.out.println(count);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider = "SubBreeds")
    public void subreedImageRandomTest(String breed,String subreed,int errorCode,String status) {
        try{
            Response response=given().log().all().when().get(baseURL+"breed/"+breed+"/"+subreed+"/images/random");
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(),errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            if(errorCode==200) {
                Assert.assertEquals(isValidURL(response.jsonPath().getString("message")), true);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test(dataProvider="Count")
    public void subreedImageRandomCountTest(double count,int errorCode,String status) {
        try{
            Response response=given().log().all().when().get(baseURL+"breed/hound/afghan/images/random/"+count);
            response.getBody().prettyPrint();
            Assert.assertEquals(response.getStatusCode(), errorCode);
            Assert.assertEquals(response.jsonPath().getString("status"),status);
            List<String> jsonResponse = response.jsonPath().getList("message");
            if(count>239){
                Assert.assertEquals(jsonResponse.size(),239);//max limit is 239 for afghan hounds
            }
            else {
                Assert.assertEquals(jsonResponse.size(),(int)count);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @DataProvider(name = "Breeds")
    public Object[][] getDataFromDataprovider1() {
        return new Object[][]
                {
                        {"bulldog", 200, "success"},
                        {"hound", 200, "success"},
                        {"mastiff", 200, "success"},
                        {"retriever", 200, "success"},
                        {"spaniel", 200, "success"},
                        {"terrier", 200, "success"},
                        {"abcd", 404, "error"},
                        {" ", 404, "error"},
                        {"!@#$&", 404, "error"},
                        {"null", 404, "error"},
                };
    }

    @DataProvider(name = "Count")
    public Object[][] getDataFromDataprovider2() {
        return new Object[][]
                {
                        {10,200,"success"},
                        {50,200,"success"},
                        {500,200,"success"},
                        {1000,200,"success"},
                        {1001,200,"success"},
                        {1.7,200,"success"},//takes minimum as 1
                        {22.5,200,"success"},//takes int value as 22
                };
    }

    @DataProvider(name = "SubBreeds")
    public Object[][] getDataFromDataprovider3() {
        return new Object[][]
                {
                        {"bulldog","english",200,"success"},
                        {"hound","blood",200,"success"},
                        {"mastiff","bull",200,"success"},
                        {"retriever","golden",200,"success"},
                        {"spaniel","cocker",200,"success"},
                        {"terrier","american",200,"success"},
                        {"abcd","abcd",404,"error"},
                        {" "," ",404,"error"},
                        {"!@#$&","^&*(",404,"error"},
                        {"null","null",404,"error"},
                };
    }

    public boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

}
