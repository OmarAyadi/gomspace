package com.example.gomspace.config;

import com.github.jmnarloch.spring.boot.modelmapper.ModelMapperConfigurer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.stereotype.Component;

@Component
public class CustomModelMapperConfig implements ModelMapperConfigurer {
    @Override
    public void configure(ModelMapper modelMapper) {
        modelMapper
                .getConfiguration()
                .setSourceNamingConvention(NamingConventions.NONE)
                .setDestinationNamingConvention(NamingConventions.NONE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }
}
