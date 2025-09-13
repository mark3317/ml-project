package ru.markn.ml.remoteData

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.io.asInputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

private const val remoteDataUrl = "https://github.com/ageron/handson-ml/raw/master/datasets/"
private const val housingUrl = "housing/housing.tgz"

private const val localDataDir = "../datasets"
private const val housingTgzFile = "housing.tgz"
private const val housingCsvFile = "housing.csv"

val remoteDataClient: HttpClient by lazy {
    HttpClient {
        defaultRequest {
            url.takeFrom(remoteDataUrl)
        }
    }
}

suspend fun getHousingTgz() : Path {
    val housingTgzPath = Path("$localDataDir/$housingTgzFile")
    val data = remoteDataClient.get(housingUrl).readBytes()
    SystemFileSystem.createDirectories(Path(localDataDir))
    SystemFileSystem.sink(housingTgzPath).buffered().use {
        it.write(data)
    }
    return housingTgzPath
}

fun extractHousingCsv(path: Path) : Path {
    val housingCsvPath = Path("$localDataDir/$housingCsvFile")
    SystemFileSystem.source(path).buffered().use { source ->
        GzipCompressorInputStream(source.asInputStream()).use { gins ->
            TarArchiveInputStream(gins).use { tais ->
                var entry = tais.nextEntry
                while (entry != null) {
                    print(entry.name)
                    if (!entry.isDirectory && entry.name.endsWith(".csv")) {
                        SystemFileSystem.sink(housingCsvPath).buffered().use { sink ->
                            sink.write(tais.readBytes())
                        }
                    }
                    entry = tais.nextEntry
                }
            }
        }
    }
    return housingCsvPath
}