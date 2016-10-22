package com.restdude.app.binding;

//@Component
public class CsvMessageConverter {//extends AbstractHttpMessageConverter<Iterable<CalipsoPersistable>> {
/*
    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

    public CsvMessageConverter() {
        super(MEDIA_TYPE);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Iterable.class.isAssignableFrom(clazz);
    }

    @Override
    protected Iterable<CalipsoPersistable> readInternal(Class<? extends Iterable<CalipsoPersistable>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Class domainClass = GenericTypeResolver.resolveTypeArgument(clazz, Iterable.class);
        domainClass.
        return null;
    }

    @Override
    protected void writeInternal(Iterable<CalipsoPersistable> calipsoPersistables, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

    }
    */
/*
    @Override
    protected void writeInternal(Iterable<CalipsoPersistable> response, HttpOutputMessage output)
            throws IOException, HttpMessageNotWritableException {
        OutputStream out = output.getBody();
        CsvWriter writer = new CsvWriter(new OutputStreamWriter(out));
        writer.writeAllRecords(reponse);
        writer.flush();
        writer.close();
    }

    @Override
    protected List<?> readInternal(Class<? extends List<?>> request,
                                   HttpInputMessage input) throws IOException,
            HttpMessageNotReadableException {
        InputStream in = input.getBody();
        CsvReader reader = new CsvReader(new InputStreamReader(in));
        List<?> records = reader.readAllRecords();

        return records;
    }
    */
}