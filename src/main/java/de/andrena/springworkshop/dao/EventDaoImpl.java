package de.andrena.springworkshop.dao;

import de.andrena.springworkshop.dto.EventDTO;
import de.andrena.springworkshop.dto.SpeakerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventDaoImpl implements EventDao {

	private final RestTemplate halCapableRestTemplate;

	private String scheme = "http";
	private String host = "localhost:8090";

	@Autowired
	public EventDaoImpl(RestTemplate halCapableRestTemplate) {
		this.halCapableRestTemplate = halCapableRestTemplate;
	}


	@Override
	public List<EventDTO> getAllEvents() {
		final String path = "/events";
		final UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.newInstance();
		final String url = apiUrlBuilder.scheme(scheme).host(host).path(path).build().toString();

		Resources<Resource<EventDTO>> eventResponse = sendRequest(url, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});

		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	private List<EventDTO> mapToDTOList(Resources<Resource<EventDTO>> eventResponse) {
		return eventResponse.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
	}

	private void injectSpeakers(Resources<Resource<EventDTO>> eventResponse) {
		if (eventResponse != null) {
			ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>> speakerType = new ParameterizedTypeReference<Resources<Resource<SpeakerDTO>>>() {
			};
			for (Resource<EventDTO> event : eventResponse) {
				String speakersUrl = event.getLink("speakers").getHref();
				Resources<Resource<SpeakerDTO>> speakerResources = sendRequest(speakersUrl, speakerType);
				if (speakerResources != null) {
					List<SpeakerDTO> speakerDTOList = speakerResources.getContent().stream().map(Resource::getContent).collect(Collectors.toList());
					event.getContent().setSpeakers(speakerDTOList);
				}
			}
		}
	}

	private <T> T sendRequest(String url, ParameterizedTypeReference<T> type) {
		ResponseEntity<T> response = halCapableRestTemplate.exchange(url, HttpMethod.GET, null, type);
		if (response != null) {
			return response.getBody();
		}
		return null;
	}

	@Override
	public List<EventDTO> getEventsWithTitleContaining(String title) {
		final String path = "/events/search/findByTitleContaining";
		final UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.newInstance();
		final String url = apiUrlBuilder.scheme(scheme).host(host).path(path).queryParam("title", title).build().toString();

		Resources<Resource<EventDTO>> eventResponse = sendRequest(url, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

	@Override
	public List<EventDTO> getEventsWithDescriptionContaining(String description) {
		final String path = "/events/search/findByDescriptionContaining";
		final UriComponentsBuilder apiUrlBuilder = UriComponentsBuilder.newInstance();
		final String url = apiUrlBuilder.scheme(scheme).host(host).path(path).queryParam("description", description).build().toString();

		Resources<Resource<EventDTO>> eventResponse = sendRequest(url, new ParameterizedTypeReference<Resources<Resource<EventDTO>>>() {
		});
		injectSpeakers(eventResponse);
		return mapToDTOList(eventResponse);
	}

}
