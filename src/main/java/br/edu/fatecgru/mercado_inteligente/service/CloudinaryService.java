package br.edu.fatecgru.mercado_inteligente.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public CloudinaryService(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	public String uploadImagem(MultipartFile arquivo) throws IOException {

		Map<?, ?> resultado = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.emptyMap());

		return resultado.get("secure_url").toString();
	}
}