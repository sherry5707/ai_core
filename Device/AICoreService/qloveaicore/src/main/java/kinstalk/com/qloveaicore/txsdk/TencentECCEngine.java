package kinstalk.com.qloveaicore.txsdk;


public class TencentECCEngine {

	static
	{
		try {
			System.loadLibrary("tencentECC");
		} catch (Exception e) {
		}

	}

	/**
	 * 生成ECDSA公钥和私钥文件存于指定路径下
	 *strFilePath    :  存放公钥(public.pem)和私钥(Private.pem)的路径
	 */
	public static native int CreateECDSAKey(String strFilePath);

	/**
	 * 根据key签名一段数据并返回结果
	 * strPrivateKeyPath     :  使用的密钥key的路径
	 * strBufferData   :  需要签名的数据
	 * 返回值 ： 签名后产生的licence字符串
	 */
	public static native String ECDSASignToBuffer(String strPrivateKeyPath, String strBufferData);

	/**
	 * 根据key签名一个文件ence并保存lic到文件
	 *  strPrivateKeyPath     :  使用的密钥key的路径
	 *  strDataFilePath      ： 文件路径
	 *  strLicencePath  ： 签名后产生的licence存放路径
	 */
	public static native int ECDSASignFileToLicenceFile(String strPrivateKeyPath, String strDataFilePath, String strLicencePath);

	/**
	 * 根据key签名一段数据并保存到文件
	 * strPrivateKeyPath     :  使用的密钥key的路径
	 * strBufferData   :  需要签名的数据
	 * strLicencePath  ： 签名后产生的licence存放路径
	 */
	public static native int ECDSASignBufferToLicenceFile(String strPrivateKeyPath, String strBufferData, String strLicencePath);


	/**
	 * 根据key签名一段数据并保存到文件
	 * strPrivateKey   :  使用的密钥key的文件内容
	 * strBufferData   :  需要签名的数据
	 * 返回  ： 签名后产生的licence内容base16编译
	 */
	public static native String ECDSASignBufferBase16(String strPrivateKey, String strBufferData);

	/**
	 * 根据key签名一段数据并编码然后保存到文件
	 * strPrivateKeyPath     :  使用的密钥key的路径
	 * strBufferData   :  需要签名的数据
	 * strLicencePath  ： 签名后产生的licence存放路径
	 */
	public static native int ECDSASignBufferBase16ToLicenceFile(String strPrivateKeyPath, String strBufferData, String strLicencePath);

	/**
	 * 验证一段签名是否合法
	 * strPublicKeyPath     :  使用的公钥key的路径
	 * strBufferData     :  需要验证的数据
	 * strLicenceData  ： 签名产生的Licence缓冲区
	 */
	public static native int ECDSAVerifyLicenceBuffer(String strPublicKeyPath, String strBufferData, String strLicenceData);

	/**
	 * 通过licence文件，公钥key验证一个文件是否合法
	 * strPublicKeyPath :  使用的公钥key的路径
	 * strDataFilePath  :  需要验证的数据文件
	 * strLicencePath  ： 签名产生的Licence文件
	 */
	public static native int ECDSAVerifyLicenceFile(String strPublicKeyPath, String strDataFilePath, String strLicencePath);

	/**
	 * 根据公钥和签名licence验证一个被base16编码过的文件是否合法
	 * strPublicKeyPath :  使用的公钥key的路径
	 * strDataFilePath  :  需要验证的数据文件
	 * strLicencePath  ： 签名产生的Licence文件
	 */
	public static native int ECDSAVerifyBase16LicenceFile(String strPublicKeyPath, String strDataFilePath, String strLicencePath);

	/**
	 * 根据服务器公钥生成共享密钥，同时生成客户公钥
	 * strSrvPublicKey    :  传入服务器公钥
	 * 返回值 ： [共享私钥][客户端公钥]
	 */
	public static native String[] GetECDHShareKeyFromSrvPublicKey(String strSrvPublicKey);

}