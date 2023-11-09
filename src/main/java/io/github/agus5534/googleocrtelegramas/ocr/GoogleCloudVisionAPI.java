package io.github.agus5534.googleocrtelegramas.ocr;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleCloudVisionAPI {
    public static List<String> performTextDetection(ByteString imageBytes, String languageHint, String languageHint2) throws AnnotateImageException, IOException {
        List<String> textos = new ArrayList<>();

        Feature feat = Feature.newBuilder()
                .setType(Feature.Type.TEXT_DETECTION)
                .setModel("builtin/latest")
                .setMaxResults(1)
                .build();

        ImageContext imageContext = ImageContext.newBuilder()
                .addRepeatedField(ImageContext.getDescriptor().findFieldByNumber(ImageContext.LANGUAGE_HINTS_FIELD_NUMBER), "es")
                .addRepeatedField(ImageContext.getDescriptor().findFieldByNumber(ImageContext.LANGUAGE_HINTS_FIELD_NUMBER), "en")
                .setCropHintsParams(CropHintsParams.newBuilder().addAllAspectRatios(Arrays.asList(0.9f)).build())
                .setWebDetectionParams(WebDetectionParams.newBuilder().setIncludeGeoResults(true).build())
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            Image img = Image.newBuilder().setContent(imageBytes).build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .setImageContext(imageContext)
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

        return textos;
    }
}
