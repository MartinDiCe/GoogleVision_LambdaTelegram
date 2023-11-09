package io.github.agus5534.googleocrtelegramas.ocr;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.utils.texts.StringToNumberConverter;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextExtractor;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextConcatenator;

import javax.imageio.ImageIO;

public class TextReader {
    public static int read(BufferedImage image) throws IOException, AnnotateImageException {
        List<String> textos = new ArrayList<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        ByteString imgBytes = ByteString.copyFrom(baos.toByteArray());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            TimingsReport.report("Request enviado");

            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(List.of(request));

            for (AnnotateImageResponse res : responses.getResponsesList()) {
                if (res.hasError()) {
                    String errorMessage = "Error: " + res.getError().getMessage();
                    throw new AnnotateImageException(errorMessage);
                }

                if (res.hasFullTextAnnotation()) {
                    String fullText = res.getFullTextAnnotation().getText();
                    textos.add(fullText);
                }
            }

            TimingsReport.report("Respuesta obtenida");
        }

        String textoAProcesar = TextConcatenator.concatenateAndClean(textos);

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
