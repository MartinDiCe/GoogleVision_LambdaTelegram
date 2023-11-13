package io.github.agus5534.googleocrtelegramas.ocr;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que encapsula la lógica para realizar la detección de texto utilizando la API de Google Cloud Vision.
 */
public class GoogleCloudVisionAPI {

    /**
     * Realiza la detección de texto utilizando la API de Google Cloud Vision.
     *
     * @param imageBytes Bytes de la imagen a procesar.
     * @return Lista de textos detectados en la imagen.
     * @throws AnnotateImageException Si ocurre un error durante la anotación de la imagen.
     * @throws IOException           Si ocurre un error de entrada/salida durante la ejecución.
     */
    public static List<String> performTextDetection(ByteString imageBytes) throws AnnotateImageException, IOException {
        List<String> textos = new ArrayList<>();

        Feature feat = Feature.newBuilder()
                .setType(Feature.Type.TEXT_DETECTION)
                .setModel("builtin/latest")
                .setMaxResults(1)
                .build();

        ImageContext imageContext = ImageContext.newBuilder()
                .addRepeatedField(ImageContext.getDescriptor().findFieldByNumber(ImageContext.LANGUAGE_HINTS_FIELD_NUMBER), "es")
                .addRepeatedField(ImageContext.getDescriptor().findFieldByNumber(ImageContext.LANGUAGE_HINTS_FIELD_NUMBER), "en")
                .setCropHintsParams(CropHintsParams.newBuilder().addAllAspectRatios(List.of(0.9f)).build())
                .setWebDetectionParams(WebDetectionParams.newBuilder().setIncludeGeoResults(true).build())
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            Image img = Image.newBuilder().setContent(imageBytes).build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .setImageContext(imageContext)
                    .build();

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

        } catch (IOException | AnnotateImageException e) {
            throw e;
        }

        return textos;
    }
}