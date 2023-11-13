# Setup inicial
Para poder ejecutar el código del proyecto debes estar autenticado contra Google Cloud API (ver ref. ); como el proyecto ya se encuentra configurado en Google Cloud sólo realiza los siguiente pasos:

- [Descarga e instala Google Cloud Vision CLI](https://cloud.google.com/sdk/docs/install?hl=es-419)
- Desde consola de comandos (CMD en Win) inicializa gcloud CLI `gcloud init` (ref. https://cloud.google.com/vision/docs/setup?hl=es-419#sdk ): se abre una solapa del navegador con el login form; pedile las credenciales de la cuenta que usamos a tu PM 

⚠️ **LAS CREDENCIALES DE TESTEO SE ARMA CREANDO UNA NUEVA CUENTA**

- Como parte del login inicial en la consola te pide seleccionar el proyecto, elige el **[4] telegramocr**
- Genera el archivo de credenciales correspondiente: `gcloud auth application-default login` (ref. https://cloud.google.com/vision/docs/setup?hl=es-419#client-library-user-account-authentication )
- Ya estas listo para correr tu código

# Google OCR Telegramas

Este script procesa imágenes de telegramas electorales utilizando el servicio OCR (Optical Character Recognition) de Google Cloud. Puede ser utilizado en entornos de servidorless como AWS Lambda.

## Funcionalidad

El script realiza las siguientes funciones:

1. **Procesamiento de Imágenes:** Utiliza el servicio OCR de Google para procesar una imagen de un telegrama electoral y extraer información relevante.

2. **Recorte de Imágenes:** Divide la imagen en secciones verticales y procesa cada sección por separado para mejorar la precisión del OCR.

3. **Conversión de Texto a Números:** Convierte cadenas de texto en números utilizando un mapeo predefinido.

4. **Limpieza de Texto:** Elimina espacios y saltos de línea adicionales en el texto procesado.

5. **Extracción de Texto:** Extrae texto del telegrama, eliminando saltos de línea y palabras demasiado largas.

## Funciones Principales

### 0. ImageManipulation

- **Propósito:** Manipula la imagen del telegrama para que mejore el resultado del OCR, incluye un recorte de la imagen verticalmente en la cantidad que quiera el usuario (Esto se debe poder cambiar o usar a gusto).

### 1. ImageProcessor

- **Propósito:** Procesa la imagen del telegrama y devuelve resultados utilizando OCR.

### 2. StringToNumberConverter

- **Propósito:** Convierte cadenas de texto en números según un mapeo predefinido.

### 3. TextCleaner

- **Propósito:** Limpia y procesa texto, encontrando la subcadena más larga dependiendo de los caracteres seteados en llamada de la clase.

### 4. TextExtractor

- **Propósito:** Extrae y procesa texto, eliminando saltos de línea y palabras demasiado largas.

### 5. ErrorLogger

- **Propósito:** Maneja errores durante el procesamiento, registrando mensajes de error y resultados.

### 6. LambdaLoggerAdapter

- **Propósito:** Adapter para utilizar SLF4J Logger como implementación de `LambdaLogger`.

## Uso

1. **Configuración de Clases:** Configura las clases según tus necesidades y mapeos específicos en `StringToNumberConverter` y otros.

2. **Configuración de Lambda:** En el entorno de AWS Lambda, establece la clase principal como `Main` y configura las entradas necesarias.

## Configuración Adicional

Puedes agregar cualquier configuración adicional necesaria para adaptar el script a tus necesidades específicas.

## Ejemplo de Configuración en AWS Lambda

1. **Entrada:**

    ```json
    {
        "image": "URL_de_la_imagen_del_telegrama"
    }
    ```

2. **Salida:**

    ```json
    {
        "results": 123
    }
    ```

3. **Logs:**

    ```
    Procesamiento de la imagen completado con éxito.
    Resultados: [123, 45, ...]
    ```

---





