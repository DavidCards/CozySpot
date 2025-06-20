package com.example.cozyspot.database.creator;

import com.example.cozyspot.database.Classes.House;
import java.util.ArrayList;
import java.util.List;

public class InitialHouses {
    public static List<House> getAll(int bobId, int dianaId) {
        List<House> houses = new ArrayList<>();
        // Lisboa
        houses.add(new House("Lisbon Central Flat 1", "Lisboa", "Apartamento moderno no centro de Lisboa, com varanda, ar-condicionado, Wi-Fi rápido, cozinha equipada e fácil acesso ao metrô. Ideal para famílias ou grupos de amigos. Aproveite a proximidade dos principais pontos turísticos, restaurantes e vida noturna da cidade. O prédio possui elevador e segurança 24h.", 120.0, 4, 4, bobId, "lisboa_central_flat1", 38.7110, -9.1420));
        houses.add(new House("Lisbon Central Flat 2", "Lisboa", "Apartamento espaçoso com varanda e vista para o rio Tejo. Possui dois quartos, sala ampla, cozinha completa e estacionamento privativo. Desfrute de uma estadia confortável com acesso rápido ao centro histórico e aos melhores restaurantes da cidade.", 140.0, 5, 5, bobId, "lisboa_central_flat2", 38.7367, -9.1250));
        houses.add(new House("Lisbon Central Flat 3", "Lisboa", "Estúdio aconchegante perto do metro, perfeito para casais ou viajantes a trabalho. Inclui máquina de lavar, TV a cabo e varanda. Localização privilegiada para explorar Lisboa a pé, com mercados e cafés próximos.", 90.0, 2, 2, bobId, "lisboa_central_flat3", 38.7482, -9.1601));
        houses.add(new House("Lisbon Central Flat 4", "Lisboa", "Apartamento com vista para o rio, decoração moderna, dois banheiros, Wi-Fi de alta velocidade e fácil acesso ao centro histórico. Ideal para famílias que buscam conforto e praticidade.", 110.0, 3, 3, bobId, "lisboa_central_flat4", 38.7075, -9.1700));
        houses.add(new House("Lisbon Central Flat 5", "Lisboa", "Flat moderno e bem localizado, próximo a restaurantes, bares e pontos turísticos. Perfeito para quem quer explorar Lisboa a pé. O apartamento oferece cozinha equipada, ar-condicionado e uma bela vista da cidade.", 130.0, 4, 4, bobId, "lisboa_central_flat5", 38.7650, -9.1012));
        // Porto
        houses.add(new House("Porto Riverside Loft 1", "Porto", "Loft com vista para o Douro, decoração industrial, cozinha americana e varanda privativa. Próximo à Ribeira e ao centro histórico. Aproveite a atmosfera vibrante do Porto com todo o conforto.", 100.0, 3, 3, dianaId, "porto_riverside_loft1", 41.1579, -8.6291));
        houses.add(new House("Porto Riverside Loft 2", "Porto", "Apartamento charmoso no centro, com dois quartos, sala de estar confortável, Wi-Fi e estacionamento gratuito. Localização excelente para explorar a cidade e suas atrações culturais.", 95.0, 2, 2, dianaId, "porto_riverside_loft2", 41.1780, -8.5970));
        houses.add(new House("Porto Riverside Loft 3", "Porto", "Flat moderno junto à Ribeira, ideal para casais. Possui cozinha equipada, ar-condicionado e fácil acesso ao transporte público. Desfrute de noites tranquilas e de uma vista incrível do rio.", 115.0, 4, 4, dianaId, "porto_riverside_loft3", 41.1340, -8.6112));
        houses.add(new House("Porto Riverside Loft 4", "Porto", "Apartamento com varanda e vista panorâmica, três quartos, dois banheiros e sala de jantar espaçosa. Perfeito para famílias ou grupos de amigos que desejam conforto e praticidade.", 150.0, 5, 5, dianaId, "porto_riverside_loft4", 41.1620, -8.6840));
        houses.add(new House("Porto Riverside Loft 5", "Porto", "Estúdio acolhedor no Porto, próximo à estação de trem, com cozinha compacta e ambiente tranquilo. Ideal para viajantes que buscam praticidade e boa localização.", 80.0, 2, 2, dianaId, "porto_riverside_loft5", 41.1480, -8.5830));
        // Faro
        houses.add(new House("Faro Beach House 1", "Faro", "Casa de praia com piscina privativa, churrasqueira, jardim amplo e acesso direto à praia. Ideal para férias em família. Aproveite o sol do Algarve com todo o conforto e privacidade.", 200.0, 6, 6, bobId, "faro_beach_house1", 37.0194, -7.9304));
        houses.add(new House("Faro Beach House 2", "Faro", "Apartamento à beira-mar, dois quartos, varanda com vista para o oceano, cozinha equipada e estacionamento. Perfeito para relaxar e curtir a praia a poucos passos de casa.", 160.0, 4, 4, bobId, "faro_beach_house2", 37.0000, -7.9500));
        houses.add(new House("Faro Beach House 3", "Faro", "Flat moderno perto da praia, com ar-condicionado, Wi-Fi, sala de estar e fácil acesso ao centro de Faro. Ideal para famílias pequenas ou casais.", 120.0, 3, 3, bobId, "faro_beach_house3", 37.0400, -7.9100));
        houses.add(new House("Faro Beach House 4", "Faro", "Casa com jardim privativo, três quartos, churrasqueira e área de lazer para crianças. Aproveite momentos inesquecíveis com sua família em um ambiente seguro e confortável.", 180.0, 5, 5, bobId, "faro_beach_house4", 37.0250, -7.9700));
        houses.add(new House("Faro Beach House 5", "Faro", "Apartamento com terraço, vista para o mar, cozinha completa e estacionamento privativo. Desfrute de dias ensolarados e noites tranquilas à beira-mar.", 110.0, 2, 2, bobId, "faro_beach_house5", 37.0600, -7.9000));
        // Braga
        houses.add(new House("Braga Family Home 1", "Braga", "Casa familiar espaçosa, com cinco quartos, jardim, piscina e área gourmet. Perfeita para grandes grupos. Aproveite a tranquilidade e o conforto em uma das melhores regiões de Braga.", 140.0, 5, 5, dianaId, "braga_family_house1", 41.5454, -8.4265));
        houses.add(new House("Braga Family Home 2", "Braga", "Apartamento moderno no centro de Braga, três quartos, varanda, cozinha equipada e garagem. Localização privilegiada para explorar a cidade a pé.", 110.0, 3, 3, dianaId, "braga_family_house2", 41.5600, -8.4300));
        houses.add(new House("Braga Family Home 3", "Braga", "Flat com varanda e vista para a cidade, quatro quartos, sala ampla e Wi-Fi rápido. Ideal para famílias ou grupos de amigos.", 120.0, 4, 4, dianaId, "braga_family_house3", 41.5300, -8.4100));
        houses.add(new House("Braga Family Home 4", "Braga", "Estúdio acolhedor em Braga, ideal para casais, com cozinha compacta e localização central. Aproveite a praticidade e o charme do centro histórico.", 85.0, 2, 2, dianaId, "braga_family_house4", 41.5700, -8.4500));
        houses.add(new House("Braga Family Home 5", "Braga", "Casa com jardim e piscina, seis quartos, área de lazer e churrasqueira. Perfeita para eventos em família ou grupos grandes.", 210.0, 6, 6, dianaId, "braga_family_house5", 41.5200, -8.4000));
        // Coimbra
        houses.add(new House("Coimbra Student Flat 1", "Coimbra", "Apartamento para estudantes, três quartos, sala de estudo, Wi-Fi e próximo à universidade. Ambiente seguro e tranquilo, ideal para quem busca praticidade.", 90.0, 3, 3, bobId, "coimbra_student_flat1", 40.2033, -8.4103));
        houses.add(new House("Coimbra Student Flat 2", "Coimbra", "Flat moderno perto da universidade, dois quartos, cozinha equipada, sala de estar e varanda. Perfeito para estudantes ou jovens profissionais.", 100.0, 2, 2, bobId, "coimbra_student_flat2", 40.2200, -8.4300));
        houses.add(new House("Coimbra Student Flat 3", "Coimbra", "Estúdio aconchegante, ideal para estudantes ou jovens profissionais, com ambiente tranquilo e seguro. Próximo a mercados, cafés e transporte público.", 70.0, 1, 1, bobId, "coimbra_student_flat3", 40.2100, -8.4000));
        houses.add(new House("Coimbra Student Flat 4", "Coimbra", "Apartamento com varanda, quatro quartos, sala ampla, cozinha completa e ótima localização. Ideal para grupos de estudantes.", 120.0, 4, 4, bobId, "coimbra_student_flat4", 40.2300, -8.4200));
        houses.add(new House("Coimbra Student Flat 5", "Coimbra", "Flat espaçoso e central, cinco quartos, sala de jantar, cozinha equipada e Wi-Fi de alta velocidade. Aproveite o melhor de Coimbra com conforto e praticidade.", 130.0, 5, 5, bobId, "coimbra_student_flat5", 40.2400, -8.4100));
        return houses;
    }
}
