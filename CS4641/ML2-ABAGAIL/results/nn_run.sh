#Generic, all against each other with default params
java  -jar Expirements.jar -i 10000 -algo rhc > output/default_rhc.txt
java  -jar Expirements.jar -i 10000 -algo sa > output/default_sa.txt
java  -jar Expirements.jar -i 10000 -algo ga > output/default_ga.txt

#Simulated annealing, change the params a bit
java  -jar Expirements.jar -i 10000 -algo sa -alpha .99 > output/sa_99.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .95 > output/sa_95.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .85  > output/sa_85.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .75  > output/sa_75.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .80  > output/sa_80.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .60  > output/sa_60.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .30  > output/sa_30.txt
java  -jar Expirements.jar -i 10000 -algo sa -alpha .05  > output/sa_5.txt

#GA, vary the params
java  -jar Expirements.jar -i 5000 -algo ga -popsize 200  > output/ga_popsize_200.txt
java  -jar Expirements.jar -i 5000 -algo ga -popsize 100  > output/ga_popsize_100.txt
java  -jar Expirements.jar -i 5000 -algo ga -popsize 300  > output/ga_popsize_300.txt

#GA, vary the params
java  -jar Expirements.jar -i 5000 -algo ga -tomate 100  > output/ga_tomate_100.txt
java  -jar Expirements.jar -i 5000 -algo ga -tomate 150  > output/ga_tomate_150.txt
java  -jar Expirements.jar -i 5000 -algo ga -tomate 50  > output/ga_tomate_50.txt

java  -jar Expirements.jar -i 5000 -algo ga -tomutate 10  > output/ga_tomutate_10.txt
java  -jar Expirements.jar -i 5000 -algo ga -tomutate 50  > output/ga_tomutate_50.txt
java  -jar Expirements.jar -i 5000 -algo ga -tomutate 100  > output/ga_tomutate_100.txt