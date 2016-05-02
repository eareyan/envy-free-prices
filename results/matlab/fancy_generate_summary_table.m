filename = '/home/eareyanv/workspace/envy-free-prices/results/results-fancy_overdemand.csv';
output_folder = '/home/eareyanv/Dropbox/CSCI2980 - Reading and Research/TADA/tables/';
strcat(output_folder,'h')
M = csvread(filename,1);
%M(:,3)
%M = M(M(:,3)==0.25,:)
%M = M(M(:,4)~=1.0,:)
ckEfficiency = mean(M(:,5));
ckRevenue = mean(M(:,6));
ckTime = mean(M(:,7));
ckWE1 = mean(M(:,8));
ckWE2 = mean(M(:,9));

lpOptEfficiency = mean(M(:,10));
lpOptRevenue = mean(M(:,11));
lpOptTime = mean(M(:,12));
lpOptWE1 = mean(M(:,13));
lpOptWE2 = mean(M(:,14));

lpWFEfficiency = mean(M(:,15));
lpWFRevenue = mean(M(:,16));
lpWFTime = mean(M(:,17));
lpWFWE1 = mean(M(:,18));
lpWFWE2 = mean(M(:,19));

lpG1Efficiency = mean(M(:,20));
lpG1Revenue = mean(M(:,21));
lpG1Time = mean(M(:,22));
lpG1WE1 = mean(M(:,23));
lpG1WE2 = mean(M(:,24));

lpG2Efficiency = mean(M(:,25));
lpG2Revenue = mean(M(:,26));
lpG2Time = mean(M(:,27));
lpG2WE1 = mean(M(:,28));
lpG2WE2 = mean(M(:,29));


%matrix = [ckEfficiency ckRevenue ckTime ckWE1 ckWE2;evpEfficiency evpRevenue evpTime evpWE1 evpWE2;mweqEfficiency mweqRevenue mweqTime mweqWE1 mweqWE2;lpEfficiency lpRevenue lpTime lpWE1 lpWE2]
%matrix = [ckEfficiency ckRevenue ckTime ckWE1 ckWE2;lpOptEfficiency lpOptRevenue lpOptTime lpOptWE1 lpOptWE2;lpWFEfficiency lpWFRevenue lpWFTime lpWFWE1 lpWFWE2;lpG1Efficiency lpG1Revenue lpG1Time lpG1WE1 lpG1WE2;lpG2Efficiency lpG2Revenue lpG2Time lpG2WE1 lpG2WE2]
matrix = [ckEfficiency ckRevenue ckTime ckWE1 ckWE2;lpOptEfficiency lpOptRevenue lpOptTime lpOptWE1 lpOptWE2;lpG1Efficiency lpG1Revenue lpG1Time lpG1WE1 lpG1WE2;lpG2Efficiency lpG2Revenue lpG2Time lpG2WE1 lpG2WE2]
rowLabels = {'CK', 'LP Optimal','LP Greedy 1','LP Greedy 2'}; 
columnLabels = {'Efficiency','Revenue','Time','EF','MC'}; 
matrix2latex(matrix, strcat(output_folder,'out.tex'), 'rowLabels', rowLabels, 'columnLabels', columnLabels, 'alignment', 'c', 'format', '%-6.2f'); 
