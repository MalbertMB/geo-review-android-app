package edu.ub.presentation.pos.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

import edu.ub.domain.model.entities.Toilet;
import edu.ub.domain.valueobjects.ClientId;
import edu.ub.domain.valueobjects.Rating;
import edu.ub.domain.model.entities.Valoration;
import edu.ub.presentation.pos.ToiletPO;
import edu.ub.presentation.pos.ValorationPO;

/*
 * This class is a singleton that provides a generic ModelMapper instance.
 */
public class DomainToPOMapper extends ModelMapper {

  private static final ModelMapper modelMapper = new ModelMapper();

  static {
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
        .setMatchingStrategy(MatchingStrategies.LOOSE);

    modelMapper.addConverter(new AbstractConverter<ClientId, String>() {
      @Override
      protected String convert(ClientId source) {
        return source != null ? source.toString() : null;
      }
    });

    modelMapper.addConverter(new AbstractConverter<ToiletPO, Toilet>() {
      @Override
      protected Toilet convert(ToiletPO source) {
        return new Toilet(
                source.getToiletUid(),
                new ClientId(source.getClientId()),
                source.getName(),
                source.getDescription(),
                source.getCoord(),
                source.getImg_url(),
                source.getRatingAverage(),
                source.getnValoration(),
                source.getHandicap(),
                source.getFree(),
                source.getBaby(),
                source.getMen(),
                source.getWomen(),
                source.getUnisex(),
                source.getValorationUidList()
        );
      }
    });

    modelMapper.addConverter(new AbstractConverter<Toilet, ToiletPO>() {
      @Override
      protected ToiletPO convert(Toilet source) {
        return new ToiletPO(
                source.getUid(),
                source.getClientId().toString(),
                source.getName(),
                source.getDescription(),
                source.getCoord(),
                source.getImg_url(),
                source.getRatingAverage(),
                source.getnValoration(),
                source.getNoLegs(),
                source.getFree(),
                source.getBaby(),
                source.getMen(),
                source.getWomen(),
                source.getUnisex(),
                source.getValorationUid()
        );
      }
    });

    modelMapper.addConverter(new AbstractConverter<ValorationPO, Valoration>() {
      @Override
      protected Valoration convert(ValorationPO source) {
        return new Valoration(
                source.getUid(),
                source.getToiletUid(),
                source.getClientId(),
                new Rating((int) source.getRating()),
                source.getComment(),
                source.getImg_Url(),
                source.getDate().toString()
        );
      }
    });

    modelMapper.addConverter(new AbstractConverter<Valoration, ValorationPO>() {
      @Override
      protected ValorationPO convert(Valoration source) {
        return new ValorationPO(
                source.getUid(),
                source.getToiletUid(),
                source.getClientId(),
                source.getRating().getValue(),
                source.getComment(),
                source.getImatgeUrl(),
                source.getDate()
        );
      }
    });

  }

  public static <S, T> T mapObject(S source, Class<T> destinationType) {
    return modelMapper.map(source, destinationType);
  }

  public static <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    return source
        .stream()
        .map(element -> modelMapper.map(element, targetClass))
        .collect(Collectors.toList());
  }
}

