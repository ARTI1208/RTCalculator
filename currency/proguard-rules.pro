-keepclassmembers class
    org.simpleframework.xml.core.AttributeLabel,
    org.simpleframework.xml.core.ElementListLabel,
    org.simpleframework.xml.core.ElementLabel {

    *;
}

-keep,allowobfuscation @interface org.simpleframework.xml.Root
-keep @org.simpleframework.xml.Root class * {*;}
-keepclasseswithmembers class * {
    @org.simpleframework.xml.Root <methods>;
    @org.simpleframework.xml.Root <fields>;
    @org.simpleframework.xml.Root <init>(...);
}

-dontwarn javax.xml.stream.Location
-dontwarn javax.xml.stream.XMLEventReader
-dontwarn javax.xml.stream.XMLInputFactory
-dontwarn javax.xml.stream.events.Attribute
-dontwarn javax.xml.stream.events.Characters
-dontwarn javax.xml.stream.events.StartElement
-dontwarn javax.xml.stream.events.XMLEvent
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE