package io.github.agus5534.googleocrtelegramas.ocr;

import com.google.protobuf.ByteString;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.utils.texts.StringToNumberConverter;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextExtractor;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextCleaner;

import javax.imageio.ImageIO;

public class TextReader {
    public static int read(BufferedImage image) throws IOException, AnnotateImageException {

        List<String> textos;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", baos);

        ByteString imgBytes = ByteString.copyFrom(baos.toByteArray());

        textos = GoogleCloudVisionAPI.performTextDetection(imgBytes, "es","en");

        String textoAProcesar = TextCleaner.concatenateAndClean(textos);

        int variable = procesarTextos(textoAProcesar);
        return variable;
    }

    private static int procesarTextos(String textos) {

            TextExtractor textExtractor = new TextExtractor(textos, 5);
            int var = StringToNumberConverter.convert(textExtractor.extractText());

            TimingsReport.report("Textos procesados");
            return var;
    }

}
