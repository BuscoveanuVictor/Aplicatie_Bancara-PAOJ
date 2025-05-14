# Folosim imaginea oficiala OpenJDK
FROM openjdk:21-jdk-slim

# Setam directorul de lucru
WORKDIR /app

# Copiem toate fisierele sursa
COPY src/ /app/src/

# Copiem fisierele de configurare
COPY lib/ /app/lib/

# Compilam toate fisierele Java cu suport pentru preview features
RUN javac --enable-preview --release 21 -cp /app/lib/* -d /app/out /app/src/Main.java /app/src/BankAccount/*.java /app/src/AppAccount/*.java /app/src/UserBankAccount/*.java /app/src/DB/*.java

# Setam comanda de executie cu suport pentru preview features
ENTRYPOINT ["java", "--enable-preview", "-cp", "/app/out:/app/lib/*", "Main"]