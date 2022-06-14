# itsy-bitsy
itsy-bitsy is a (very) parallel and (very) simple web crawler. Powered by zio 2, it initially started as a project to 
port [this](https://www.youtube.com/watch?v=08eSR0vn4Vw) amazing lecture by John De Goes from scalaz-zio into latest zio version.
As such, many similarities can be found with the original code.

## Configuration
There can be found several fields inside the configuration file:

| Field        | Default Value                                                                | Explanation                  | Type             |
|--------------|------------------------------------------------------------------------------|------------------------------|------------------|
| fiber-count  | 100                                                                          | Number of fibers to execute  | Integer          |
| seeds        |  https://www.google.com, https://www.facebook.com, https://www.microsoft.com | Seed links to crawl          | Array of Strings |

## Running
1. Before running the crawler, make sure you supply two methods with these type signatures:

```scala
Router: URL => Set[URL]
```
and
```scala
Processor: (URL, Document) => Task[Unit]
```
First method takes care of the links to follow, and the second method takes care of how the content is being processed.
For example, if you want to filter some links with some predicate, Router should be used, whereas if you want to process
 the content of the page (ie. save contents to database), Processor should be used. These signatures can be found and edited
inside Main.scala.

2. After setting proper methods, run the project with sbt.
