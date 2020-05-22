package app.solocoin.solocoin.util

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Saurav Gupta on 21/05/2020
 */
interface KParcelable : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int)
}

/*
 * Creator Factory Functions
 */
inline fun <reified T> parcelableCreator(
    crossinline create: (Parcel) -> T
) =
    object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel) = create(source)
        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }

inline fun <reified T> parcelableClassLoaderCreator(
    crossinline create: (Parcel, ClassLoader) -> T
) =
    object : Parcelable.ClassLoaderCreator<T> {
        override fun createFromParcel(source: Parcel, loader: ClassLoader) =
            create(source, loader)

        override fun createFromParcel(source: Parcel) =
            createFromParcel(source, T::class.java.classLoader!!)

        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }

