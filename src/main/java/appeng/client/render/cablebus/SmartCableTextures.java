package appeng.client.render.cablebus;


import java.util.Arrays;

import com.google.common.base.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import appeng.core.AppEng;


/**
 * Manages the channel textures for smart cables.
 */
public class SmartCableTextures
{

	public static final ResourceLocation[] SMART_CHANNELS_TEXTURES = {
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_00" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_01" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_02" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_03" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_04" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_10" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_11" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_12" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_13" ),
			new ResourceLocation( AppEng.MOD_ID, "parts/cable/smart/channels_14" )
	};

	// Textures used to display channels on smart cables. There's two sets of 5 textures each, and
	// one of each set are composed together to get even/odd colored channels
	private final TextureAtlasSprite[] textures;

	public SmartCableTextures( Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter )
	{
		textures = Arrays.stream( SMART_CHANNELS_TEXTURES )
				.map( bakedTextureGetter::apply )
				.toArray( TextureAtlasSprite[]::new );
	}

	/**
	 * The odd variant is used for displaying channels 1-4 as in use.
	 */
	public TextureAtlasSprite getOddTextureForChannels( int channels )
	{
		if( channels < 0 )
		{
			return textures[0];
		}
		else if( channels <= 4 )
		{
			return textures[channels];
		}
		else
		{
			return textures[4];
		}
	}

	/**
	 * The odd variant is used for displaying channels 5-8 as in use.
	 */
	public TextureAtlasSprite getEvenTextureForChannels( int channels )
	{
		if( channels < 5 )
		{
			return textures[5];
		}
		else if( channels <= 9 )
		{
			return textures[channels];
		}
		else
		{
			return textures[9];
		}
	}
}