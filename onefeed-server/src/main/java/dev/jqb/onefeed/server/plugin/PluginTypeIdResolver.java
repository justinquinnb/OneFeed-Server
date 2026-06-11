package dev.jqb.onefeed.server.plugin;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;

/**
 * The means by which Jackson automatically resolves class definitions provided by plugins when
 * deserializing
 */
public class PluginTypeIdResolver extends TypeIdResolverBase {
    private final PluginTypeRegistry registry;
    private JavaType baseType;

    public PluginTypeIdResolver(PluginTypeRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public String idFromValue(DatabindContext ctxt, Object value) throws JacksonException {
        return value.getClass().getName();
    }

    @Override
    public JavaType typeFromId(DatabindContext ctxt, String id) throws JacksonException {
        Class<?> subtype = registry.resolve(id);
        if (subtype == null) {
            return null;
        }
        return ctxt.constructSpecializedType(baseType, subtype);
    }

    @Override
    public String idFromValueAndType(DatabindContext ctxt, Object value, Class<?> suggestedType)
        throws JacksonException {
        return (value != null) ? value.getClass().getName() : suggestedType.getName();
    }

    @Override
    public Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
