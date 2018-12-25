package org.stepio.aws.lambda.json;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.stepio.aws.lambda.json.test.ABody;
import org.stepio.aws.lambda.json.test.APart;
import org.stepio.aws.lambda.json.test.AResult;
import org.stepio.aws.lambda.json.test.StringLambdaHandler;
import org.stepio.aws.lambda.json.test.StringLambdaHandlerExplicit;
import org.stepio.aws.lambda.json.test.TestLambdaHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class DefaultLambdaHandlerTest {

    private Context context;
    private StringLambdaHandler stringHandler;
    private TestLambdaHandler testHandler;
    private DefaultLambdaHandler<ABody, AResult> defaultHandler;

    private APIGatewayProxyResponseEvent response;
    private APIGatewayProxyRequestEvent request;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.stringHandler = new StringLambdaHandler();
        this.testHandler = new TestLambdaHandler();
        this.defaultHandler = new DefaultLambdaHandler<>(ABody.class, AResult.class);
    }

    @Test
    public void constructor_generics() {
        StringLambdaHandlerExplicit stringHandlerExplicit = new StringLambdaHandlerExplicit();
        assertThat(this.stringHandler.bodyClass).isSameAs(stringHandlerExplicit.bodyClass);
        assertThat(this.stringHandler.responseClass).isSameAs(stringHandlerExplicit.responseClass);
        assertThat(this.stringHandler.responseClass).isSameAs(stringHandlerExplicit.responseClass);
    }

    @Test
    public void reader_caches() {
        ObjectReader reader1 = this.defaultHandler.reader(ABody.class);
        ABody body = dummy();
        ObjectReader reader2 = this.defaultHandler.reader(body.getClass());
        assertThat(reader2).isSameAs(reader1);
    }

    @Test
    public void writer_caches() {
        ObjectWriter writer1 = this.defaultHandler.writer(ABody.class);
        ABody body = dummy();
        ObjectWriter writer2 = this.defaultHandler.writer(body.getClass());
        assertThat(writer2).isSameAs(writer1);
    }

    @Test
    public void writeDummy_thenRead() throws IOException {
        ABody body = dummy();
        String text = this.defaultHandler.writer(body.getClass())
                .writeValueAsString(body);
        ABody other = this.defaultHandler.reader(ABody.class)
                .readValue(text);
        assertThat(other).isEqualToComparingFieldByFieldRecursively(body);
    }

    @Test
    public void writeEmpty_thenRead() throws IOException {
        ABody empty = new ABody();
        ObjectReader reader = this.defaultHandler.reader(ABody.class);
        ABody other = reader.readValue("{}");
        assertThat(other).isEqualToComparingFieldByFieldRecursively(empty);
    }

    @Test
    public void handleRequest_GET() {

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET")
                .withBody("{\"name\":\"DUMMY#1\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#1\"}");

        response = this.defaultHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void handleRequest_POST() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody("{\"name\":\"fail\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody("{\"name\":\"DUMMY#2\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#2\"}");

        response = this.defaultHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void handleRequest_PUT() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withBody("{\"name\":\"fail\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT")
                .withBody("{\"name\":\"DUMMY#3\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#3\"}");

        response = this.defaultHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void handleRequest_DELETE() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withBody("{\"name\":\"fail\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE")
                .withBody("{\"name\":\"DUMMY#4\"}");
        response = this.testHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#4\"}");

        response = this.defaultHandler.handleRequest(request, this.context);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void body_empty() {
        request = new APIGatewayProxyRequestEvent();
        assertThat(this.stringHandler.body(request)).isNull();
    }

    @Test
    public void body_text() {
        request = new APIGatewayProxyRequestEvent()
                .withBody("Request, world!");
        assertThat(this.stringHandler.body(request)).isEqualTo("Request, world!");
    }

    @Test
    public void body_entity() {
        assertThatThrownBy(() -> this.testHandler.body(
                new APIGatewayProxyRequestEvent().withBody("{name:dummy}")
        )).isInstanceOf(IllegalArgumentException.class).hasMessageStartingWith("Failed to extract class org.stepio.aws.lambda.json.test.ABody from request body");

        request = new APIGatewayProxyRequestEvent()
                .withBody("{\"name\":\"Bond007\"}");
        ABody body = this.testHandler.body(request);
        assertThat(body)
                .hasFieldOrPropertyWithValue("name", "Bond007")
                .hasFieldOrPropertyWithValue("quantity", null)
                .hasFieldOrPropertyWithValue("elements", null);
    }

    @Test
    public void wrap_empty() {
        response = this.stringHandler.wrap(null);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 204);
        response = this.stringHandler.wrap("");
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 204);
    }

    @Test
    public void wrap_text() {
        response = this.stringHandler.wrap("Response, world!");
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 200)
                .hasFieldOrPropertyWithValue("body", "Response, world!");
    }

    @Test
    public void wrap_entity() {
        AResult result = new AResult();
        result.setValue("Joy42");
        response = this.testHandler.wrap(result);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 200)
                .hasFieldOrPropertyWithValue("body", "{\"value\":\"Joy42\"}");
    }

    private ABody dummy() {
        ABody body = new ABody();
        body.setName("dummy");
        body.setQuantity(42);
        List<APart> elements = new ArrayList<>();
        APart element = new APart();
        element.setValue("hello");
        elements.add(element);
        element = new APart();
        element.setValue("world");
        elements.add(element);
        body.setElements(elements);
        return body;
    }
}
