# Etapa 1: build da aplicação usando Maven + Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia o pom.xml e resolve dependências (melhora cache)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código fonte
COPY src ./src

# Gera o .jar (sem rodar testes pra ficar mais rápido)
RUN mvn -B clean package -DskipTests

# Etapa 2: imagem final, mais leve, só com o JRE 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o jar gerado na etapa anterior
COPY --from=builder /app/target/*.jar /app/app.jar

# Limita um pouco a memória por causa da VM free (1GB)
ENV JAVA_OPTS="-Xms256m -Xmx400m"
# ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Porta padrão do Spring Boot (ajuste se seu app usar outra)
EXPOSE 8080

# Comando pra rodar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
