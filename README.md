binder
======

Binder is an annotations-based POJO to CSV converter utility inspired by Apache Camel Bindy.


usage
======

Given the POJO Below.

```
@CsvRecord(separator = ",", generateHeaderColumns = true)
public class MagicPlayer {

  @DataField(pos = 1)
  private String name;

  @DataField(pos = 2)
  private int skillLevel;

  @DataField(pos = 3)
  private String FavoriteCard;

  @DataField(pos = 4)
  private boolean NoDice;

  @DataField(pos = 5)
  private String StringOptimalPlayTime;
  
  //...Beloved Getters/Setter
}  
```

You can convert it to CSV by doing:

```
  MagicPlayer player = new MagicPlayer();
  player.setName("RSID");
  player.setSkillLevel(4);
  player.setNoDice(true);
  player.setFavoriteCard("Ashnod's Coupon");
  player.setStringOptimalPlayTime("After Work");
  
  File file = new File("c:\\thePlayer.csv");
  
  FileOutputStream fop = new FileOutputStream(file);
  BinderCsvDataFormat bindy = new BinderCsvDataFormat(ModelPackage);
  bindy.marshal(player, fop);
```

and you're Done!


Road Map
======

- [ ] Tests for all the key functionality.
- [ ] Wiki/documentation pages
- [ ] Support multiple CSVRecord types per package.
- [ ] Refactor the core CSV engine.
- [ ] Stabilize the API
- [ ] Implement JAX-RS provider wrapper.
- [ ] Implement Apache Camel Data Format wrapper.


