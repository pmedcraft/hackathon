package com.fidelity.sdlweb;

import com.google.gson.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Phil Medcraft
 *
 */
public class ContentServiceClient {
    private static final Logger LOG = Logger.getLogger(ContentServiceClient.class);

    private static final int TEXT_FIELD = 0;
    private static final int RTF_FIELD = 2;
    private static final int EMBEDDED_FIELD = 4;
    private static final int LINKED_COMPONENT_FIELD = 5;

    private static final String ENDPOINT_URL = "http://54.75.250.223:8081/client/v2/content.svc/PageContents" +
            "(PageId=%1$s,PublicationId=%2$s)";

    public ContentServiceClient() {
        super();
    }

    public static void main(String[] args) {
        ContentServiceClient client = new ContentServiceClient();
        client.requestPageContent("754", "15");
    }

    public List<Component> requestPageContent(String pageId, String publicationId) {
        List<Component> components = new ArrayList<Component>();
        String url = String.format(ENDPOINT_URL, pageId, publicationId);
        HttpRequest request = HttpRequest.get(url, false, "$format", "json");

        if (request.ok() && request.code() == 200) {
            Gson gson = new GsonBuilder().create();
            JsonObject jsonResponse = gson.fromJson(request.body(), JsonObject.class);

            JsonElement rootElement = jsonResponse.get("d");
            if (rootElement != null) {
                JsonElement contentElement = new JsonParser().parse(rootElement.getAsJsonObject()
                        .get("Content").getAsString());
                JsonArray componentPresentations = contentElement.getAsJsonObject()
                        .get("ComponentPresentations").getAsJsonArray();
                for (int i=0; i < componentPresentations.size(); i++) {
                    JsonElement jsonComponentPresentation = componentPresentations.get(i);
                    JsonObject jsonComponent = jsonComponentPresentation.getAsJsonObject()
                            .get("Component").getAsJsonObject();
                    JsonObject fields = jsonComponent.get("Fields").getAsJsonObject();
                    if (fields != null) {
                        Component component = new Component();
                        component.setId(jsonComponent.get("Title").getAsString());
                        component.setFields(processFields(new HashMap<String, String>(), fields));
                        components.add(component);
                    }
                }
            }
        }

        return components;
    }

    private Map<String,String> processFields(Map<String,String> fieldValues, JsonObject fieldEntries) {
        Set<Map.Entry<String, JsonElement>> entries = fieldEntries.entrySet();
        for (Map.Entry<String, JsonElement> entry: entries) {
            String fieldName = entry.getKey();
            String fieldValue = null;

            JsonElement fieldEntry = entry.getValue();
            int fieldType = fieldEntry.getAsJsonObject().get("FieldType").getAsInt();
            switch (fieldType) {
                case TEXT_FIELD: {
                    fieldValue = fieldEntry.getAsJsonObject().get("Values") != null ?
                            fieldEntry.getAsJsonObject().get("Values").getAsString() : null;
                    break;
                }
                case RTF_FIELD: {
                    fieldValue = fieldEntry.getAsJsonObject().get("Values") != null ?
                            fieldEntry.getAsJsonObject().get("Values").getAsString() : null;
                    break;
                }
                case EMBEDDED_FIELD: {
                    JsonElement embeddedValuesElement = fieldEntry.getAsJsonObject()
                            .get("EmbeddedValues");
                    if (embeddedValuesElement != null) {
                        JsonArray embeddedValues = embeddedValuesElement.getAsJsonArray();
                        for (int i=0; i < embeddedValues.size(); i++) {
                            JsonObject embeddedEntry = embeddedValues.get(i).getAsJsonObject();
                            processFields(fieldValues, embeddedEntry);
                        }
                    }
                    break;
                }
                case LINKED_COMPONENT_FIELD: {
                    JsonElement linkedComponentValuesElement = fieldEntry.getAsJsonObject()
                            .get("LinkedComponentValues");
                    if (linkedComponentValuesElement != null) {
                        JsonArray linkedComponentValues = linkedComponentValuesElement
                                .getAsJsonArray();
                        for (int i=0; i < linkedComponentValues.size(); i++) {
                            JsonObject linkedComponentEntry = linkedComponentValues.get(i)
                                    .getAsJsonObject();
                            JsonObject multimediaEntry = linkedComponentEntry
                                    .getAsJsonObject("Multimedia");
                            fieldValue = multimediaEntry.get("Url").getAsString();
                        }
                    }
                    break;
                }
                default: fieldValue = null;
            }

            fieldValues.put(fieldName, fieldValue);
        }

        return fieldValues;
    }
}