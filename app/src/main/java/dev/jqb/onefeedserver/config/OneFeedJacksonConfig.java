//package dev.jqb.onefeed.server.config;
//
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import dev.jqb.onefeed.server.plugin.PluginTypeIdResolver;
//import dev.jqb.onefeed.server.plugin.PluginTypeRegistry;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
//
///**
// * Tells Jackson how to handle external classes
// * {@link PluginTypeRegistry}
// */
//@Configuration
//public class OneFeedJacksonConfig {
//
//    private final PluginTypeIdResolver pluginTypeIdResolver;
//
//    @Autowired
//    public OneFeedJacksonConfig(PluginTypeIdResolver pluginTypeIdResolver) {
//        this.pluginTypeIdResolver = pluginTypeIdResolver;
//    }
//
//    @Bean
//    public JsonMapperBuilderCustomizer customTypeResolverCustomizer() {
//        return builder -> {
//            StdTypeResolverBuilder typer = new StdTypeResolverBuilder();
//            JsonTypeInfo.Value settings = JsonTypeInfo.Value.construct(
//                JsonTypeInfo.Id.CUSTOM,
//                JsonTypeInfo.As.PROPERTY,
//                "@type",
//                null,
//                false,
//                null
//            );
//
//            typer.init(settings, pluginTypeIdResolver);
//            builder.setDefaultTyping(typer);
//        };
//    }
//}
