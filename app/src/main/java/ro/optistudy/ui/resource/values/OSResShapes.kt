package ro.optistudy.ui.resource.values

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ro.optistudy.ui.resource.values.OSResDimens

object OSResShapes {

    object Common{
        val shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(24.dp)
        )
    }

    object Space{
        val H4 = Modifier.height(OSResDimens.dp4)
        val H18 = Modifier.height(OSResDimens.dp18)
        val H20 = Modifier.height(OSResDimens.dp20)
        val H24 = Modifier.height(OSResDimens.dp24)
        val H28 = Modifier.height(OSResDimens.dp28)
        val H32 = Modifier.height(OSResDimens.dp32)
        val H48 = Modifier.height(OSResDimens.dp48)
        val H56 = Modifier.height(OSResDimens.dp56)
        val H64 = Modifier.height(OSResDimens.dp64)



        val W18 = Modifier.width(OSResDimens.dp18)
        val W20 = Modifier.width(OSResDimens.dp20)
        val W24 = Modifier.width(OSResDimens.dp24)
        val W28 = Modifier.width(OSResDimens.dp28)
        val W32 = Modifier.width(OSResDimens.dp32)
    }
}