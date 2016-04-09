function [] = graphit(x,y,z,tmin,tmax,fignum,tit)
tx = tmin:1:tmax;
ty = tmin:1:tmax;

[qx, qy] = meshgrid(tx,ty);
qz = griddata(x,y,z,qx,qy); 

figure(fignum);

contourf(qx, qy, qz, 'ShowText', 'on');%auto defined step size
title(tit);
end