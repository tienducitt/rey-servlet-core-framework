/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 *
 * @author ducnt3
 */
public class JSONUtils {
        private static Gson GSON = null;
	private static final Map<Enum<?>, String> MAP_ENUM_TO_SERIALIZED_NAME = new HashMap<Enum<?>, String>();

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(BufferedImage.class, new BufferedImageDeserializer());
		gsonBuilder.registerTypeAdapter(BufferedImage.class, new BufferedImageSerializer());
		GSON = gsonBuilder.create();
	}

	public static class BufferedImageDeserializer implements JsonDeserializer<BufferedImage> {
		@Override
		public BufferedImage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
			String strBase64 = (String) ctx.deserialize(json, String.class);
			if (strBase64.startsWith("data:image/")) {
				strBase64 = strBase64.substring(strBase64.indexOf(",") + 1, strBase64.length());
			}
			return decodeToImage(strBase64);
		}
	}

	public static class BufferedImageSerializer implements JsonSerializer<BufferedImage> {

		@Override
		public JsonElement serialize(BufferedImage src, Type typeOfSrc, JsonSerializationContext ctx) {
			return new JsonPrimitive(encodeToString(src, "jpg"));
		}
	}

	private static String encodeToString(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = Base64.encodeBase64String(imageBytes);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	private static BufferedImage decodeToImage(String imageString) {
		BufferedImage image = null;
		byte[] imageByte;
		try {
			imageByte = Base64.decodeBase64(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static <T> T fromJson(BufferedReader reader, Type type) {
		return GSON.fromJson(reader, type);
	}

	public static <T> String toJson(T object) {
		return GSON.toJson(object);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return GSON.fromJson(json, clazz);
	}

	public static <T> T serializedNameToEnum(String name, Class<T> clazz) {
		return fromJson("\"" + name + "\"", clazz);
	}
}
