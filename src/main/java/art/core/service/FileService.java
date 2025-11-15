package art.core.service;

import art.core.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// Сохранение данных
//
//При выходе из приложения сохранять данные кошелька пользователя в файл.
//При авторизации загружать данные кошелька из файла.
// Сделать методы на выгрузку и загрузку файлов. Т.е. мы загружаем файл в поток и читаем его данные и их выгружаем
public class FileService {
    Path paths = Paths.get("users.data");
    public void save(Map<String, User> users)  {
        File newfile = null;
        try {
            if (!Files.exists(paths)) {
                newfile = Files.createFile(paths).toFile();
            }else{
                Files.delete(paths);
                newfile = Files.createFile(paths).toFile();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newfile))) {
            oos.writeObject(users);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, User> load() {
        File path = paths.toFile();
        //Map<String, User> users = new HashMap<>();
        if(!path.exists()) {
            return null;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (HashMap<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //Сделать метод для JSON загрузки и выгрузки
    public void saveJSON(User user) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            mapper.writeValue(new File(user.getUsername() + ".json"), user);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public User loadJSON(File file) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(file, User.class);
        }catch (IOException e){
            System.out.println("Json file could not be loaded");
            return null;
        }
    }
}
