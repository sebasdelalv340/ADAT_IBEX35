import java.io.BufferedReader
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.notExists

fun main() {

    val rutaFichero = Path.of("src/main/resources/cotizaciones.csv")

    fun comprobarFichero(ruta: Path): Boolean {
        return if (ruta.exists()) {
            true
        } else {
            false
        }
    }


    fun obtenerCotizaciones(ruta: Path): MutableMap<String, MutableMap<String, String>> {

        val mapaEmpresas = mutableMapOf<String, MutableMap<String, String>>()

        if (comprobarFichero(ruta)) {
            val br: BufferedReader = Files.newBufferedReader(ruta)
            val headers = br.readLine().split(";")

            br.useLines { lines ->
                lines.forEach { line ->
                    val values = line.split(";")
                    val key = values[0]

                    val fila = mutableMapOf<String, String>()

                    for (i in 1 until values.size) {
                        fila[headers[i]] = values[i]
                    }
                    mapaEmpresas[key] = fila
                }
            }
        }
        return mapaEmpresas
    }

    val cotizacionesMapa = obtenerCotizaciones(rutaFichero)


    fun imprimirMapa(mapa: MutableMap<String, MutableMap<String, String>>) {
        for ((key, valueMap) in mapa) {
            println(key)
            valueMap.forEach { header, value ->
                println("  $header: $value")
            }
        }
    }

    fun createFile(ruta: Path) {
        if (ruta.notExists()) {
            Files.createDirectories(ruta.parent)
            Files.createFile(ruta)
        }
    }

    fun escribirFichero(mapa: MutableMap<String, MutableMap<String, String>>) {
        val rutaNueva = Path.of("src/main/resources/cotizacionMedia.csv")

        createFile(rutaNueva)

        val columnas = "Empresa;Minimo;Maximo;Media\n"

        val bw: BufferedWriter = Files.newBufferedWriter(rutaNueva)

        bw.use { flujo ->
            flujo.write(columnas)

            for ((key, valueMap) in mapa) {

                val minimo = valueMap["Minimo"]?.replace(".", "")?.replace(",", ".")?.toDouble() ?: 0.0
                val maximo = valueMap["Maximo"]?.replace(".", "")?.replace(",", ".")?.toDouble() ?: 0.0

                val media = (minimo + maximo) / 2
                val mediaFormateada = String.format("%.2f", media).replace(",", ".")

                flujo.write("$key;$minimo;$maximo;$mediaFormateada\n")
            }
        }
    }

    imprimirMapa(cotizacionesMapa)

    escribirFichero(cotizacionesMapa)


}