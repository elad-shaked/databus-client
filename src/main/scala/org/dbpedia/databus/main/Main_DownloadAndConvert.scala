package org.dbpedia.databus.main

import better.files.File
import org.dbpedia.databus.FileHandler
import org.dbpedia.databus.cli.CLIConf
import org.dbpedia.databus.sparql.QueryHandler

object Main_DownloadAndConvert {

  def main(args: Array[String]) {

    println("Welcome to DBPedia - Download and Convert Tool")
    println("\n--------------------------------------------------------\n")

    val conf = new CLIConf(args)
    val temp_dir_download = "./tempdir_downloaded_files/"
    val dataId_string = "dataid.ttl"

    //Test if query is a File or a Query
    var queryString:String = ""
    File(conf.query()).exists() match {
      case true => {
        // "./src/query/query"
        val file = File(conf.query())
        queryString = FileHandler.readQueryFile(file)
      }
      case false => {
        queryString = conf.query()
      }
    }

    //    println(s"download query: \n\n$queryString")
    //    println("--------------------------------------------------------\n")

    println("Downloader:\n")
    println("files to download:")
    QueryHandler.executeDownloadQuery(queryString, temp_dir_download)

    println("\n--------------------------------------------------------\n")

    //  if no compression wanted (output_compression not set) change the value to an empty string
    val outputCompression = conf.output_compression.isEmpty match {
      case true => ""
      case false => conf.output_compression()
    }

    println("Converter:\n")
    val dir = File(temp_dir_download)
    val files = dir.listRecursively.toSeq
    for (file <- files) {
        if (! file.isDirectory){
          if (!file.name.equals(dataId_string)){
            println(s"input file:\t\t${file.pathAsString}")
            FileHandler.convertFile(file, temp_dir_download, conf.destination_dir(), conf.output_format(), outputCompression )
          }
        }
    }
  }

}