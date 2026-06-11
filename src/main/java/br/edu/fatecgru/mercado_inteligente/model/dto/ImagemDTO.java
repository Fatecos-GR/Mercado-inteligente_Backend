package br.edu.fatecgru.mercado_inteligente.model.dto;

public class ImagemDTO {

	private String url;
	private String publicId;

	public ImagemDTO(String url, String publicId) {
		this.url = url;
		this.publicId = publicId;
	}

	public String getUrl() {
		return url;
	}

	public String getPublicId() {
		return publicId;
	}
}