import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class Basic {

    public static void main(String[] args) {
        // formListPerson();
       // formListPersonREST();

    }

    static void hello() {
        var app = Javalin.create()
                .get("/", ctx -> ctx.result("Hello world"))
                .get("/hello/{name}", ctx -> ctx.result("Hello " + ctx.pathParam("name")))
                .start(7070);
    }

    static void getPage(){
        var app = Javalin.create(
                config -> config.addStaticFiles("src\\main\\resources", Location.EXTERNAL))
                .get("/", ctx -> ctx.html(new String(Files.readAllBytes(Paths.get("src/main/resources/html/hello.html")))))
                .start(7070);
    }

    static void getPerson(){
        Person person = new Person("noname");
        var app = Javalin.create()
                .get("/", ctx -> ctx.json(person))
                .start(7070);
    }

    static void formPerson(){
        Set<Person> persons = new HashSet<>();
        var app = Javalin.create(config -> config.enableCorsForAllOrigins())
                .post("/", ctx -> {
                    Person person = new Person(ctx.formParam("name"));
                    persons.add(person);
                    ctx.status(201);
                    persons.forEach(System.out::println);
                    ctx.redirect("/");
                })
                //.get("/", ctx -> ctx.json(persons))
                .get("/", ctx -> ctx.html(new String(Files.readAllBytes(Paths.get("src/main/resources/html/temp.html")))))
                .get("/list",ctx -> ctx.json(persons))
                .start(7070);

        /*
        fetch('
http://localhost:7070
',{
        method:'POST',
        headers:{

        },
        body:JSON.stringify({'name':'noname','surname':'surname'})
        })
        */
    }

    static void formListPerson(){
        Set<Person> persons = new HashSet<>();
        generatePage(persons);
        var app = Javalin.create(config->config.enableCorsForAllOrigins())
                .post("/", ctx -> {
                    Person person = new Person(ctx.formParam("name"), ctx.formParam("surname"));
                    persons.add(person);
                    ctx.status(201);
                    persons.forEach(System.out::println);
                    ctx.redirect("/");
                    generatePage(persons);
                })
                .get("/", ctx -> ctx.html(new String(Files.readAllBytes(Paths.get("src/main/resources/html/temp.html")))))
                .start(7070);
    }

    static void generatePage(Set<Person> persons){
        //Set<Person> persons = new HashSet<>();
        //Скопировать исходный файл во временный файл
        Path sourceFile = Paths.get("src\\main\\resources\\html\\formList.html");
        Path targetFile = Paths.get("src\\main\\resources\\html\\temp.html");
        try {
            Files.copy(sourceFile, targetFile,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
        }

        //Прочитать временный файл
        Charset charset = StandardCharsets.UTF_8;
        //Заменить в нем строку {{persons}} на данные
        ObjectMapper mapper = new ObjectMapper();
        String content = null;
        try {
            content = new String(Files.readAllBytes(targetFile), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            content = content.replace("{{persons}}", mapper.writeValueAsString(persons));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            Files.write(targetFile, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void formListPersonREST(){
        Set<Person> persons = new HashSet<>();
        var app = Javalin.create(config->config.enableCorsForAllOrigins())
                .post("/", ctx -> {
                    Person person = new Person(ctx.formParam("name"), ctx.formParam("surname"));
                    persons.add(person);
                    ctx.status(201);
                    persons.forEach(System.out::println);
                    ctx.redirect("/");
                    generatePage(persons);
                })
                .get("/", ctx -> ctx.html(new String(Files.readAllBytes(Paths.get("src/main/resources/html/formList.html")))))
                .get("/api", ctx->ctx.json(persons))
                .start(7070);
    }

}
class Person {
    public String name;
    public String surname;

    public Person() {
    }

    //К чему приводит неполная инициализация полей
    public Person(String name) {


        this.name
                = name;
    }
    public Person(String name, String surname) {


        this.name
                = name;
        this.surname = surname;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}