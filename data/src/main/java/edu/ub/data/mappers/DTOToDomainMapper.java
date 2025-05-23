package edu.ub.data.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ub.data.dtos.firestore.ToiletFirestoreDto;
import edu.ub.data.dtos.firestore.ValorationFirestoreDto;
import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Comment;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.valueobjects.ToiletUid;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.domain.valueobjects.ValorationUid;

public class DTOToDomainMapper extends ModelMapper {

    private static final ModelMapper modelMapper = new ModelMapper();
    public DTOToDomainMapper() {
        super();

        super.getConfiguration()
                .setFieldMatchingEnabled(true) // No need to define setters
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        //Conversió entre tipus no coincidents

        super.createTypeMap(ToiletFirestoreDto.class, Toilet.class)
                .setConverter(context -> {
                    ToiletFirestoreDto dto = context.getSource();
                    List<String> valorationIds = dto.getValorationUids() != null ? dto.getValorationUids() : new ArrayList<>();
                    List<ValorationUid> valorationUids = valorationIds.stream()
                            .map(ValorationUid::fromString)
                            .collect(Collectors.toList());
                    return new Toilet(
                            ToiletUid.fromString(dto.getUid()),
                            ClientId.fromString(dto.getClientId()),
                            dto.getName(),
                            dto.getDescription(),
                            dto.getCoord(),
                            dto.getImg_url(),
                            dto.getRatingAverage(),
                            dto.getnValoration(),
                            dto.isMen(),
                            dto.isWomen(),
                            dto.isUnisex(),
                            dto.isHandicap(),
                            dto.isFree(),
                            dto.isBaby(),
                            valorationUids
                    );
                });

        super.createTypeMap(ValorationFirestoreDto.class, Valoration.class)
                .setConverter(context -> {
                    ValorationFirestoreDto dto = context.getSource();
                    try {
                        return new Valoration(
                                ValorationUid.fromString(dto.getUid()),
                                ToiletUid.fromString(dto.getToiletUid()),
                                ClientId.fromString(dto.getClientId()),
                                new Rating(dto.getRating()),
                                new Comment(dto.getComment() != null ? dto.getComment() : "..."), // Handle null comments
                                dto.getDate() != null ? dto.getDate() : LocalDateTime.now().toString(), // Handle null dates (get current date)
                                dto.getImatgeUrl() != null ? dto.getImatgeUrl() : "default" // Handle null image URLs
                        );
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error mapping ValorationFirestoreDto to Valoration: " + e.getMessage(), e);
                    }
                });

        super.addConverter(new AbstractConverter<String, ClientId>() {
            @Override
            protected ClientId convert(String source) {
                return new ClientId(source);
            }
        });


        super.addConverter(new AbstractConverter<String, ToiletUid>() {
            @Override
            protected ToiletUid convert(String source) {
                return ToiletUid.fromString(source);
            }
        });

        super.addConverter(new AbstractConverter<ClientId, String>() {
            @Override
            protected String convert(ClientId clientId) {
                return clientId.toString();
            }
        });


        super.addConverter(new AbstractConverter<ToiletUid, String>() {
            @Override
            protected String convert(ToiletUid toiletUid) {
                return toiletUid.getUid();
            }
        });

        super.addConverter(new AbstractConverter<ValorationUid, String>() {
            @Override
            protected String convert(ValorationUid source) {
                return source.getUID();
            }
        });

        super.addConverter(new AbstractConverter<String, ValorationUid>() {
            @Override
            protected ValorationUid convert(String source) {
                return ValorationUid.fromString(source);
            }
        });


    }

    /*
     * Mapeja un objecte a un altre objecte de la classe especificada.
     * No caldria definir-lo perquè la classe ModelMapper ja té un mètode
     * amb la mateixa signatura, però així podem fer que la classe implementi
     * l'interfície DataMapper i veiem que si no re-aprofitéssim ModelMapper
     * hauríem de definir-lo.
    */


    public static <S, T> T mapObject(S source, Class<T> destinationType) {
        return new DTOToDomainMapper().map(source, destinationType);
    }

}
