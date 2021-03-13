package com.yosidozli.api;

import com.yosidozli.EventRecord;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.hibernate.validator.constraints.NotEmpty;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@Path("/")
public class EndPoint {
    @NotNull
    private final DeviceDao deviceDao;
    @NotNull
    private final KafkaProducer<String,EventRecord> producer;
    @NotEmpty
    private final String topic;
    @NotEmpty
    private final String table;
    public EndPoint(DeviceDao deviceDao, KafkaProducer<String, EventRecord> producer, String topic, String table) {
        this.deviceDao = deviceDao;
        this.producer = producer;
        this.topic = topic;
        this.table = table;
    }

    @POST
    @Path("/send/{uuid}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_OCTET_STREAM})
    public Response add(@PathParam("uuid") String uuid, @Context HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        byte[] bytes = IOUtils.toByteArray(request.getInputStream());
        EventRecord eventRecord = new EventRecord(uuid, Instant.now().toEpochMilli(), ByteBuffer.wrap( bytes));
        ProducerRecord<String,EventRecord> producerRecord = new ProducerRecord<>(topic,uuid,eventRecord);
        Future<RecordMetadata> result = producer.send(producerRecord);
        return Response.ok().entity(result.get()).build();
    }

    @GET
    @Path("/state")
    public  Response  fetch(@QueryParam("uuid") String uuid) {
        return Response.ok().entity(deviceDao.deviceState(table,uuid)).build();
//        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

}
