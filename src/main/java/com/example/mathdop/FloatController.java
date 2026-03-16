package com.example.mathdop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import java.util.Scanner;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class FloatController {

    @Autowired
    private FloatService service;

    @PostMapping(value = "/solve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> solveFromFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пуст");
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(content);

            String dataType = scanner.next().trim();

            if (!dataType.equalsIgnoreCase("float16")) {
                return ResponseEntity.badRequest()
                        .body("в данной версии реализован только тип float16. Запрошен: " + dataType);
            }

            int n = scanner.nextInt();

            float[][] A = new float[n][n];
            float[] b = new float[n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A[i][j] = scanner.nextFloat();
                }
                b[i] = scanner.nextFloat();
            }
            scanner.close();

            String result = service.solve(n, A, b);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ошибка парсинга файла: " + e.getMessage() +
                    "\nубедитесь, что формат соответствует.");
        }
    }
}