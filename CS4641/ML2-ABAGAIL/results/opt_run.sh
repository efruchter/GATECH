#Generic, all against each other with default params
java  -jar Expirements.jar -i 1000 -opt -problem tsp > output/default_tsp.txt
java  -jar Expirements.jar -i 1000 -opt -problem co > output/default_co.txt
java  -jar Expirements.jar -i 1000 -opt -problem cp > output/default_cp.txt

#SA

java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .99 > output/co_sa_99.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .95 > output/co_sa_95.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .85  > output/co_sa_85.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .75  > output/co_sa_75.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .80  > output/co_sa_80.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .60  > output/co_sa_60.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .30  > output/co_sa_30.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem co -alpha .05  > output/co_sa_5.txt

java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .99 > output/cp_sa_99.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .95 > output/cp_sa_95.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .85  > output/cp_sa_85.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .75  > output/cp_sa_75.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .80  > output/cp_sa_80.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .60  > output/cp_sa_60.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .30  > output/cp_sa_30.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem cp -alpha .05  > output/cp_sa_5.txt

java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .99 > output/tsp_sa_99.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .95 > output/tsp_sa_95.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .85  > output/tsp_sa_85.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .75  > output/tsp_sa_75.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .80  > output/tsp_sa_80.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .60  > output/tsp_sa_60.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .30  > output/tsp_sa_30.txt
java  -jar Expirements.jar -i 1000 -algo sa -opt -problem tsp -alpha .05  > output/tsp_sa_5.txt

#GA
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem co -tomutate 10  > output/co_ga_tomutate_10.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem co -tomutate 50  > output/co_ga_tomutate_50.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem co -tomutate 100  > output/co_ga_tomutate_100.txt

java  -jar Expirements.jar -i 1000 -algo ga -opt -problem cp -tomutate 10  > output/cp_ga_tomutate_10.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem cp -tomutate 50  > output/cp_ga_tomutate_50.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem cp -tomutate 100  > output/cp_ga_tomutate_100.txt

java  -jar Expirements.jar -i 1000 -algo ga -opt -problem tsp -tomutate 10  > output/tsp_ga_tomutate_10.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem tsp -tomutate 50  > output/tsp_ga_tomutate_50.txt
java  -jar Expirements.jar -i 1000 -algo ga -opt -problem tsp -tomutate 100  > output/tsp_ga_tomutate_100.txt

#MIMIC
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -sample 100  > output/co_mimic_sample_100.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -sample 200  > output/co_mimic_sample_200.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -sample 300  > output/co_mimic_sample_300.txt

java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -sample 100  > output/cp_mimic_sample_100.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -sample 200  > output/cp_mimic_sample_200.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -sample 300  > output/cp_mimic_sample_300.txt

java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -sample 100  > output/tsp_mimic_sample_100.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -sample 200  > output/tsp_mimic_sample_200.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -sample 300  > output/tsp_mimic_sample_300.txt

#mimic tokeep
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -tokeep 10  > output/co_mimic_tokeep_10.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -tokeep 20  > output/co_mimic_tokeep_20.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem co -tokeep 30  > output/co_mimic_tokeep_30.txt

java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -tokeep 10  > output/cp_mimic_tokeep_10.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -tokeep 20  > output/cp_mimic_tokeep_20.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem cp -tokeep 30  > output/cp_mimic_tokeep_30.txt

java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -tokeep 10  > output/tsp_mimic_tokeep_10.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -tokeep 20  > output/tsp_mimic_tokeep_20.txt
java  -jar Expirements.jar -i 1000 -algo mimic -opt -problem tsp -tokeep 30  > output/tsp_mimic_tokeep_30.txt