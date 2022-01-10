package com.thevoxelbox.voxelmap.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;

public class MapData {
   private static int DATABITS = 16;
   private static int HEIGHTPOS = 0;
   private static int MATERIALPOS = 1;
   private static int METADATAPOS = 2;
   private static int TINTPOS = 3;
   private static int LIGHTPOS = 4;
   private static int OCEANFLOORHEIGHTPOS = 5;
   private static int OCEANFLOORMATERIALPOS = 6;
   private static int OCEANFLOORMETADATAPOS = 7;
   private static int OCEANFLOORTINTPOS = 8;
   private static int OCEANFLOORLIGHTPOS = 9;
   private static int TRANSPARENTHEIGHTPOS = 10;
   private static int TRANSPARENTIDPOS = 11;
   private static int TRANSPARENTMETADATAPOS = 12;
   private static int TRANSPARENTTINTPOS = 13;
   private static int TRANSPARENTLIGHTPOS = 14;
   private static int BIOMEIDPOS = 15;
   public MapData.Point[][] points;
   public ArrayList segments;
   private int width;
   private int height;
   private Object dataLock = new Object();
   private Object labelLock = new Object();
   private int[] data;
   private ArrayList labels = new ArrayList();

   public MapData(int width, int height) {
      this.width = width;
      this.height = height;
      this.data = new int[width * height * DATABITS];
   }

   public int getHeight(int x, int z) {
      return this.getData(x, z, HEIGHTPOS);
   }

   public int getMaterial(int x, int z) {
      return this.getData(x, z, MATERIALPOS);
   }

   public int getMetadata(int x, int z) {
      return this.getData(x, z, METADATAPOS);
   }

   public int getBiomeTint(int x, int z) {
      return this.getData(x, z, TINTPOS);
   }

   public int getLight(int x, int z) {
      return this.getData(x, z, LIGHTPOS);
   }

   public int getOceanFloorHeight(int x, int z) {
      return this.getData(x, z, OCEANFLOORHEIGHTPOS);
   }

   public int getOceanFloorMaterial(int x, int z) {
      return this.getData(x, z, OCEANFLOORMATERIALPOS);
   }

   public int getOceanFloorMetadata(int x, int z) {
      return this.getData(x, z, OCEANFLOORMETADATAPOS);
   }

   public int getOceanFloorBiomeTint(int x, int z) {
      return this.getData(x, z, OCEANFLOORTINTPOS);
   }

   public int getOceanFloorLight(int x, int z) {
      return this.getData(x, z, OCEANFLOORLIGHTPOS);
   }

   public int getTransparentHeight(int x, int z) {
      return this.getData(x, z, TRANSPARENTHEIGHTPOS);
   }

   public int getTransparentId(int x, int z) {
      return this.getData(x, z, TRANSPARENTIDPOS);
   }

   public int getTransparentMetadata(int x, int z) {
      return this.getData(x, z, TRANSPARENTMETADATAPOS);
   }

   public int getTransparentBiomeTint(int x, int z) {
      return this.getData(x, z, TRANSPARENTTINTPOS);
   }

   public int getTransparentLight(int x, int z) {
      return this.getData(x, z, TRANSPARENTLIGHTPOS);
   }

   public int getBiomeID(int x, int z) {
      return this.getData(x, z, BIOMEIDPOS);
   }

   public int getData(int x, int z, int bit) {
      int index = (x + z * this.width) * DATABITS + bit;
      return this.data[index];
   }

   public void setHeight(int x, int z, int value) {
      this.setData(x, z, HEIGHTPOS, value);
   }

   public void setMaterial(int x, int z, int value) {
      this.setData(x, z, MATERIALPOS, value);
   }

   public void setMetadata(int x, int z, int value) {
      this.setData(x, z, METADATAPOS, value);
   }

   public void setBiomeTint(int x, int z, int value) {
      this.setData(x, z, TINTPOS, value);
   }

   public void setLight(int x, int z, int value) {
      this.setData(x, z, LIGHTPOS, value);
   }

   public void setOceanFloorHeight(int x, int z, int value) {
      this.setData(x, z, OCEANFLOORHEIGHTPOS, value);
   }

   public void setOceanFloorMaterial(int x, int z, int value) {
      this.setData(x, z, OCEANFLOORMATERIALPOS, value);
   }

   public void setOceanFloorMetadata(int x, int z, int value) {
      this.setData(x, z, OCEANFLOORMETADATAPOS, value);
   }

   public void setOceanFloorBiomeTint(int x, int z, int value) {
      this.setData(x, z, OCEANFLOORTINTPOS, value);
   }

   public void setOceanFloorLight(int x, int z, int value) {
      this.setData(x, z, OCEANFLOORLIGHTPOS, value);
   }

   public void setTransparentHeight(int x, int z, int value) {
      this.setData(x, z, TRANSPARENTHEIGHTPOS, value);
   }

   public void setTransparentId(int x, int z, int value) {
      this.setData(x, z, TRANSPARENTIDPOS, value);
   }

   public void setTransparentMetadata(int x, int z, int value) {
      this.setData(x, z, TRANSPARENTMETADATAPOS, value);
   }

   public void setTransparentBiomeTint(int x, int z, int value) {
      this.setData(x, z, TRANSPARENTTINTPOS, value);
   }

   public void setTransparentLight(int x, int z, int value) {
      this.setData(x, z, TRANSPARENTLIGHTPOS, value);
   }

   public void setBiomeID(int x, int z, int value) {
      this.setData(x, z, BIOMEIDPOS, value);
   }

   public void setData(int x, int z, int bit, int value) {
      int index = (x + z * this.width) * DATABITS + bit;
      this.data[index] = value;
   }

   public void moveX(int offset) {
      Object var2 = this.dataLock;
      synchronized(this.dataLock) {
         if (offset > 0) {
            System.arraycopy(this.data, offset * DATABITS, this.data, 0, this.data.length - offset * DATABITS);
         } else if (offset < 0) {
            System.arraycopy(this.data, 0, this.data, -offset * DATABITS, this.data.length + offset * DATABITS);
         }

      }
   }

   public void moveZ(int offset) {
      Object var2 = this.dataLock;
      synchronized(this.dataLock) {
         if (offset > 0) {
            System.arraycopy(this.data, offset * this.width * DATABITS, this.data, 0, this.data.length - offset * this.width * DATABITS);
         } else if (offset < 0) {
            System.arraycopy(this.data, 0, this.data, -offset * this.width * DATABITS, this.data.length + offset * this.width * DATABITS);
         }

      }
   }

   public void segmentBiomes() {
      this.points = new MapData.Point[this.width][this.height];
      this.segments = new ArrayList();

      int x;
      for(int x = 0; x < this.width; ++x) {
         for(x = 0; x < this.height; ++x) {
            this.points[x][x] = new MapData.Point(x, x, this.getBiomeID(x, x));
         }
      }

      Object var7 = this.dataLock;
      synchronized(this.dataLock) {
         for(x = 0; x < this.width; ++x) {
            for(int z = 0; z < this.height; ++z) {
               if (!this.points[x][z].inSegment) {
                  MapData.Segment segment = new MapData.Segment(this.points[x][z]);
                  this.segments.add(segment);
                  segment.flood();
               }
            }
         }

      }
   }

   public void findCenterOfSegments() {
      if (this.segments != null) {
         for(int t = 0; t < this.segments.size(); ++t) {
            MapData.Segment segment = (MapData.Segment)this.segments.get(t);
            if (segment.biome != -1) {
               segment.calculateCenter();
            }
         }
      }

      Object var7 = this.labelLock;
      synchronized(this.labelLock) {
         this.labels.clear();
         if (this.segments != null) {
            for(int t = 0; t < this.segments.size(); ++t) {
               MapData.Segment segment = (MapData.Segment)this.segments.get(t);
               if (segment.biome != -1) {
                  MapData.BiomeLabel label = new MapData.BiomeLabel();
                  label.biomeInt = segment.biome;
                  label.size = segment.memberPoints.size();
                  label.x = segment.centerX;
                  label.z = segment.centerZ;
                  this.labels.add(label);
               }
            }
         }

      }
   }

   public ArrayList getBiomeLabels() {
      ArrayList labelsToReturn = new ArrayList();
      Object var2 = this.labelLock;
      synchronized(this.labelLock) {
         labelsToReturn.addAll(this.labels);
         return labelsToReturn;
      }
   }

   public class BiomeLabel {
      public int biomeInt = -1;
      public int size = 0;
      public int x = 0;
      public int z = 0;
   }

   public class Segment {
      public ArrayList memberPoints;
      public int biome;
      public int centerX = 0;
      public int centerZ = 0;
      ArrayList currentShell;

      public Segment(MapData.Point point) {
         this.biome = point.biome;
         this.memberPoints = new ArrayList();
         this.memberPoints.add(point);
         this.currentShell = new ArrayList();
      }

      public void flood() {
         ArrayList candidatePoints = new ArrayList();
         candidatePoints.add(this.memberPoints.remove(0));

         while(candidatePoints.size() > 0) {
            MapData.Point point = (MapData.Point)candidatePoints.remove(0);
            point.isCandidate = false;
            if (point.biome == this.biome) {
               this.memberPoints.add(point);
               point.inSegment = true;
               boolean edge = false;
               MapData.Point neighbor;
               if (point.x < MapData.this.width - 1) {
                  neighbor = MapData.this.points[point.x + 1][point.z];
                  if (!neighbor.inSegment && !neighbor.isCandidate) {
                     candidatePoints.add(neighbor);
                     neighbor.isCandidate = true;
                  }

                  if (neighbor.biome != point.biome) {
                     edge = true;
                  }
               } else {
                  edge = true;
               }

               if (point.x > 0) {
                  neighbor = MapData.this.points[point.x - 1][point.z];
                  if (!neighbor.inSegment && !neighbor.isCandidate) {
                     candidatePoints.add(neighbor);
                     neighbor.isCandidate = true;
                  }

                  if (neighbor.biome != point.biome) {
                     edge = true;
                  }
               } else {
                  edge = true;
               }

               if (point.z < MapData.this.height - 1) {
                  neighbor = MapData.this.points[point.x][point.z + 1];
                  if (!neighbor.inSegment && !neighbor.isCandidate) {
                     candidatePoints.add(neighbor);
                     neighbor.isCandidate = true;
                  }

                  if (neighbor.biome != point.biome) {
                     edge = true;
                  }
               } else {
                  edge = true;
               }

               if (point.z > 0) {
                  neighbor = MapData.this.points[point.x][point.z - 1];
                  if (!neighbor.inSegment && !neighbor.isCandidate) {
                     candidatePoints.add(neighbor);
                     neighbor.isCandidate = true;
                  }

                  if (neighbor.biome != point.biome) {
                     edge = true;
                  }
               } else {
                  edge = true;
               }

               if (edge) {
                  point.layer = 0;
                  this.currentShell.add(point);
               }
            }
         }

      }

      public void calculateCenter() {
         this.calculateCenterOfMass();
         this.morphologicallyErode();
      }

      public void calculateCenterOfMass() {
         this.calculateCenterOfMass(this.memberPoints);
      }

      public void calculateCenterOfMass(Collection points) {
         this.centerX = 0;
         this.centerZ = 0;

         MapData.Point point;
         for(Iterator var2 = points.iterator(); var2.hasNext(); this.centerZ += point.z) {
            point = (MapData.Point)var2.next();
            this.centerX += point.x;
         }

         this.centerX /= points.size();
         this.centerZ /= points.size();
      }

      public void calculateClosestPointToCenter(Collection points) {
         int distanceSquared = 131072;
         MapData.Point centerPoint = null;
         Iterator var4 = points.iterator();

         while(var4.hasNext()) {
            MapData.Point point = (MapData.Point)var4.next();
            int pointDistanceSquared = (point.x - this.centerX) * (point.x - this.centerX) + (point.z - this.centerZ) * (point.z - this.centerZ);
            if (pointDistanceSquared < distanceSquared) {
               distanceSquared = pointDistanceSquared;
               centerPoint = point;
            }
         }

         this.centerX = centerPoint.x;
         this.centerZ = centerPoint.z;
      }

      public void morphologicallyErode() {
         float labelWidth = (float)(Minecraft.getMinecraft().fontRenderer.getStringWidth(BiomeGenBase.getBiomeGenArray()[this.biome].biomeName) + 8);
         float multi = (float)(MapData.this.width / 32);
         float shellWidth = 2.0F;
         float labelPadding = labelWidth / 16.0F * multi / shellWidth;

         int layer;
         for(layer = 0; this.currentShell.size() > 0 && (float)layer < labelPadding; this.currentShell = this.getNextShell(this.currentShell, layer)) {
            ++layer;
         }

         if (this.currentShell.size() > 0) {
            ArrayList remainingPoints = new ArrayList();
            Iterator var7 = this.memberPoints.iterator();

            while(true) {
               MapData.Point point;
               do {
                  if (!var7.hasNext()) {
                     this.calculateClosestPointToCenter(remainingPoints);
                     return;
                  }

                  point = (MapData.Point)var7.next();
               } while(point.layer >= 0 && point.layer != layer);

               remainingPoints.add(point);
            }
         }
      }

      public ArrayList getNextShell(Collection pointsToCheck, int layer) {
         ArrayList nextShell = new ArrayList();
         Iterator var4 = pointsToCheck.iterator();

         while(var4.hasNext()) {
            MapData.Point point = (MapData.Point)var4.next();
            MapData.Point neighbor;
            MapData.Point neighbor2;
            if (point.x < MapData.this.width - 2) {
               neighbor = MapData.this.points[point.x + 1][point.z];
               neighbor2 = MapData.this.points[point.x + 2][point.z];
               if (neighbor.biome == point.biome && neighbor.layer < 0 && neighbor2.biome == point.biome && neighbor2.layer < 0) {
                  neighbor.layer = layer;
                  neighbor2.layer = layer;
                  nextShell.add(neighbor2);
               } else if (neighbor.biome == point.biome && neighbor.layer < 0) {
                  neighbor.layer = layer;
                  nextShell.add(neighbor);
               } else if (neighbor2.biome == point.biome && neighbor2.layer < 0) {
                  neighbor2.layer = layer;
                  nextShell.add(neighbor2);
               }
            }

            if (point.x > 1) {
               neighbor = MapData.this.points[point.x - 1][point.z];
               neighbor2 = MapData.this.points[point.x - 2][point.z];
               if (neighbor.biome == point.biome && neighbor.layer < 0 && neighbor2.biome == point.biome && neighbor2.layer < 0) {
                  neighbor.layer = layer;
                  neighbor2.layer = layer;
                  nextShell.add(neighbor2);
               } else if (neighbor.biome == point.biome && neighbor.layer < 0) {
                  neighbor.layer = layer;
                  nextShell.add(neighbor);
               } else if (neighbor2.biome == point.biome && neighbor2.layer < 0) {
                  neighbor2.layer = layer;
                  nextShell.add(neighbor2);
               }
            }

            if (point.z < MapData.this.height - 1) {
               neighbor = MapData.this.points[point.x][point.z + 1];
               if (neighbor.biome == point.biome && neighbor.layer < 0) {
                  neighbor.layer = layer;
                  nextShell.add(neighbor);
               }
            }

            if (point.z > 0) {
               neighbor = MapData.this.points[point.x][point.z - 1];
               if (neighbor.biome == point.biome && neighbor.layer < 0) {
                  neighbor.layer = layer;
                  nextShell.add(neighbor);
               }
            }
         }

         if (nextShell.size() > 0) {
            return nextShell;
         } else {
            this.calculateCenterOfMass(pointsToCheck);
            this.calculateClosestPointToCenter(pointsToCheck);
            return nextShell;
         }
      }
   }

   private class Point {
      public int x;
      public int z;
      public boolean inSegment = false;
      public boolean isCandidate = false;
      public boolean isEroded = false;
      public int layer = -1;
      public int biome = -1;

      public Point(int x, int z, int biome) {
         this.x = x;
         this.z = z;
         this.biome = biome;
      }
   }
}
