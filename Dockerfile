FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY . .

RUN mkdir -p bin && javac -cp "lib/*" -d bin src/**/*.java

CMD ["java", "-cp", "bin:lib/*", "benchmark.BenchmarkRunner"]
