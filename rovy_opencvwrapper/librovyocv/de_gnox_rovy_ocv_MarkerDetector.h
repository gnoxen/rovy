/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class de_gnox_rovy_ocv_MarkerDetector */

#ifndef _Included_de_gnox_rovy_ocv_MarkerDetector
#define _Included_de_gnox_rovy_ocv_MarkerDetector
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     de_gnox_rovy_ocv_MarkerDetector
 * Method:    nInit
 * Signature: (ZII)I
 */
JNIEXPORT jint JNICALL Java_de_gnox_rovy_ocv_MarkerDetector_nInit
  (JNIEnv *, jobject, jboolean, jint, jint);

/*
 * Class:     de_gnox_rovy_ocv_MarkerDetector
 * Method:    nDetectMarkers
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_de_gnox_rovy_ocv_MarkerDetector_nDetectMarkers
  (JNIEnv *, jobject);

/*
 * Class:     de_gnox_rovy_ocv_MarkerDetector
 * Method:    nReleaseCamera
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_de_gnox_rovy_ocv_MarkerDetector_nReleaseCamera
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
