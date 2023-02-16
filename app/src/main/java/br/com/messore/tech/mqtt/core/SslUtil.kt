package br.com.messore.tech.mqtt.core

import android.content.Context
import br.com.messore.tech.mqtt.R
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.security.KeyStore
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

object SslUtil {

    /** uncomment this method when need to use certificates, don't forget to adding files in raw folder **/
//    fun getCertificates(context: Context): Triple<InputStream, InputStream, InputStream> {
//        val caCrtFile = context.resources.openRawResource(R.raw.ca)
//        val crtFile = context.resources.openRawResource(R.raw.cert)
//        val keyFile = context.resources.openRawResource(R.raw.key)
//        return Triple(caCrtFile, crtFile, keyFile)
//    }

    fun getSocketFactory(caCrtFile: InputStream, crtFile: InputStream, keyFile: InputStream, password: String): SSLSocketFactory {
        val bouncy = BouncyCastleProvider()
        Security.addProvider(bouncy)

        var caCert: X509Certificate? = null
        var bis = BufferedInputStream(caCrtFile)
        val cf = CertificateFactory.getInstance("X.509")

        while (bis.available() > 0) {
            caCert = cf.generateCertificate(bis) as X509Certificate
        }

        bis = BufferedInputStream(crtFile)
        var cert: X509Certificate? = null
        while (bis.available() > 0) {
            cert = cf.generateCertificate(bis) as X509Certificate
        }

        val pemParser = PEMParser(InputStreamReader(keyFile))
        val `object` = pemParser.readObject()
        val converter = JcaPEMKeyConverter().setProvider(bouncy)
        val key = converter.getKeyPair(`object` as PEMKeyPair)

        val caKs = KeyStore.getInstance(KeyStore.getDefaultType())
        caKs.load(null, null)
        caKs.setCertificateEntry("cert-certificate", caCert)
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(caKs)

        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        ks.setCertificateEntry("certificate", cert)
        ks.setKeyEntry("private-cert", key.private, password.toCharArray(), arrayOf(cert))
        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, password.toCharArray())

        val context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.keyManagers, tmf.trustManagers, null)

        return context.socketFactory

    }
}
