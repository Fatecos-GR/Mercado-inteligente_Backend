package br.edu.fatecgru.mercado_inteligente.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import br.edu.fatecgru.mercado_inteligente.model.dto.ImagemDTO;

@Service
public class ImagemService {

	private final Cloudinary cloudinary;

	public ImagemService(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	public ImagemDTO salvarImagem(MultipartFile imagem, String pasta) throws Exception {

		if (imagem == null || imagem.isEmpty()) {
			return null;
		}

		String tipo = imagem.getContentType();

		if (tipo == null || !tipo.startsWith("image/")) {
			throw new IllegalArgumentException("Arquivo enviado não é uma imagem.");
		}

		Map<?, ?> resultado = cloudinary.uploader().upload(imagem.getBytes(), ObjectUtils.asMap("folder", pasta));

		String url = resultado.get("secure_url").toString();
		String publicId = resultado.get("public_id").toString();

		return new ImagemDTO(url, publicId);
	}

	public void deletarImagem(String publicId) {

		if (publicId == null || publicId.isBlank()) {
			return;
		}

		try {

			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

		} catch (Exception e) {
			System.out.println("Erro ao deletar imagem: " + e.getMessage());
		}
	}

	public ImagemDTO substituirImagem(String publicIdAntigo, MultipartFile novaImagem, String pasta) throws Exception {

		if (novaImagem == null || novaImagem.isEmpty()) {
			return null;
		}

		ImagemDTO nova = salvarImagem(novaImagem, pasta);

		if (publicIdAntigo != null && !publicIdAntigo.isBlank()) {
			deletarImagem(publicIdAntigo);
		}

		return nova;
	}

}