package com.example.mathdop;

import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class FloatService {

    public String solve(int n, float[][] rawA, float[] rawB) {
        //перевожу в float16
        HalfPrecision[][] A = new HalfPrecision[n][n];
        HalfPrecision[] b = new HalfPrecision[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = new HalfPrecision(rawA[i][j]);
            }
            b[i] = new HalfPrecision(rawB[i]);
        }

        makeDiagonallyDominant(A, b, n);

        HalfPrecision[][] C = new HalfPrecision[n][n];
        HalfPrecision[] d = new HalfPrecision[n];
        //преобразование к виду x = Cx + d
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    C[i][j] = new HalfPrecision(0f);
                } else {
                    HalfPrecision minusAij = new HalfPrecision(0f).sub(A[i][j]);
                    C[i][j] = minusAij.div(A[i][i]);
                }
            }
            d[i] = b[i].div(A[i][i]);
        }
        //создаю массив для хранения корней
        HalfPrecision[] x = new HalfPrecision[n];
        for (int i = 0; i < n; i++) {
            x[i] = d[i];
        }

        HalfPrecision epsilon = new HalfPrecision(0.001f);
        int maxIters = 1000;

        int iterations = 0;
        //метод простых итераций
        for (int m = 0; m < maxIters; m++) {
            iterations++;
            HalfPrecision[] xNew = new HalfPrecision[n];
            HalfPrecision maxDiff = new HalfPrecision(0f);

            for (int i = 0; i < n; i++) {
                HalfPrecision sum = new HalfPrecision(0f);
                for (int j = 0; j < n; j++) {
                    sum = sum.add(C[i][j].mul(x[j]));
                }
                xNew[i] = sum.add(d[i]);

                HalfPrecision diff = xNew[i].sub(x[i]).abs();
                if (diff.get() > maxDiff.get()) {
                    maxDiff = diff;
                }
            }

            x = xNew;

            if (maxDiff.get() < epsilon.get()) {
                break;
            }
        }
        //невязка
        HalfPrecision maxResidual = new HalfPrecision(0f);
        for (int i = 0; i < n; i++) {
            HalfPrecision sum = new HalfPrecision(0f);
            for (int j = 0; j < n; j++) {
                HalfPrecision originalAij = new HalfPrecision(rawA[i][j]);
                sum = sum.add(originalAij.mul(x[j]));
            }
            HalfPrecision originalBi = new HalfPrecision(rawB[i]);
            HalfPrecision res = sum.sub(originalBi).abs();

            if (res.get() > maxResidual.get()) {
                maxResidual = res;
            }
        }
        //ответ
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append(String.format(Locale.US, "%.30f ", x[i].get()));
        }
        sb.append("\n");

        sb.append("итераций: ").append(iterations).append("\n");

        sb.append(String.format(Locale.US, "невязка: %.1e", maxResidual.get()));

        return sb.toString();
    }

    private void makeDiagonallyDominant(HalfPrecision[][] A, HalfPrecision[] b, int n) {
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            float maxVal = A[i][i].abs().get();
            for (int k = i + 1; k < n; k++) {
                float val = A[k][i].abs().get();
                if (val > maxVal) {
                    maxVal = val;
                    maxRow = k;
                }
            }
            if (maxRow != i) {
                HalfPrecision[] tempA = A[i];
                A[i] = A[maxRow];
                A[maxRow] = tempA;

                HalfPrecision tempB = b[i];
                b[i] = b[maxRow];
                b[maxRow] = tempB;
            }
        }
    }
}